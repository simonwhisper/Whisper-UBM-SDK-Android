package com.whisper.whisperubmexample

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.whisper.whisperubmexample.ui.theme.WhisperUBMExampleTheme
import com.whisper.whisperubmandroid.WhisperUBM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    var whisperUBM: WhisperUBM? = null
    private val _uiState = MutableStateFlow(listOf<String>())
    val uiState: StateFlow<List<String>> = _uiState.asStateFlow()
    val context = this
    private val _permission = MutableStateFlow(false)
    val permissionState: StateFlow<Boolean> = _permission.asStateFlow()


    override fun onCreate(savedInstanceState: Bundle?) {

        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }



        super.onCreate(savedInstanceState)
        requestMicPermission()
        //enableEdgeToEdge()
        setContent {

            window?.statusBarColor = Color.Black.toArgb()

            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            var listening: Boolean? by remember{
                mutableStateOf(null)
            }
            var permission = permissionState.collectAsState()

            WhisperUBMExampleTheme {
                
                Column (Modifier.background(Color.Black)){
                    Box (
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ){
                        when(listening){
                            null -> SettingsRow(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(vertical = 20.dp),
                                startButton = { hash, prefix ->
                                    if(!permission.value){
                                        return@SettingsRow
                                    }

                                    try {
                                        whisperUBM = WhisperUBM(
                                            hash = hash,
                                            prefix = prefix,
                                            context = context
                                        )
                                        whisperUBM?.setCallBack { ubmString ->
                                            _uiState.update {
                                                it + arrayListOf(ubmString)
                                            }
                                            vib.vibrate(200)
                                        }
                                        listening = false
                                    }catch (e: Exception){
                                        println(e)
                                        vib.vibrate(200)
                                    }
                                },
                                permission = permission.value
                            )
                            else -> {
                                Box (
                                    Modifier
                                        .align(Alignment.TopStart)
                                        .fillMaxWidth()
                                        .height(screenHeight - 75.dp)
                                ){
                                    MessageList(messages = uiState)
                                }
                                OnOff(
                                    listening = listening,
                                    startButton = {
                                        if(listening == true) {
                                            whisperUBM?.stopListening()
                                            listening = false
                                        }else{
                                            whisperUBM?.startListening()
                                            listening = true
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(vertical = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //permissions
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            _permission.value = isGranted
        }

    fun requestMicPermission(){
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}

//ui
@Composable
fun ListRow(message: String){
    Text(
        text = message,
        color = Color.White
    )
}

@Composable
fun MessageList(messages: StateFlow<List<String>>){
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val messagesState = messages.collectAsState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(15.dp),
        horizontalAlignment = Alignment.Start
    ) {
        for (message in messagesState.value){
            ListRow(message = message)
        }
    }

    LaunchedEffect(messagesState.value.size){
        coroutineScope.launch {
            scrollState.scrollBy(999999f) //why is there no .scrollToBottom()!?!?!?
        }
    }

}

@Composable
fun OnOff(listening: Boolean?, startButton: () -> Unit, modifier: Modifier){

    Row(
        modifier//.padding(vertical = 40.dp, horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(25))
                .size(65.dp)
                .background(
                    if (listening == true) {
                        Color.Green
                    } else {
                        Color.Red
                    }
                )
                .clickable {
                    startButton()
                }
                .padding(10.dp)

        ) {
            Text(
                if(listening == true){
                    "ON"
                }else{
                    "OFF"
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun SettingsRow(startButton: (String, String) -> Unit, modifier: Modifier, permission: Boolean){
    val sharedPref: SharedPreferences = LocalContext.current.getSharedPreferences("myPref", MODE_PRIVATE)
    var hash = remember{ mutableStateOf(sharedPref.getString("hash", "") ?: "") }
    var prefix = remember{ mutableStateOf(sharedPref.getString("prefix", "") ?: "") }


    Row(
        modifier
    ) {

        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 10.dp)
        ) {
            Row (Modifier.padding(vertical = 20.dp)) {
                Text("PERMISSION: ", color = Color.White)
                Text(text = if(permission){"Granted"}else{"Denied"}
                    , color = Color.White)
            }
            Row () {
                Text("HASH:", color = Color.White)
                TextField(
                    hash.value,
                    { newText: String ->
                        hash.value = newText.toString()
                        sharedPref.edit().putString("hash", hash.value).apply()
                    }
                )
            }
            Row (Modifier.padding(vertical = 20.dp)) {
                Text("PREFIX:", color = Color.White)
                TextField(
                    prefix.value,
                    { newText: String ->
                        prefix.value = newText.toString()
                        sharedPref.edit().putString("prefix", prefix.value).apply()
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25))
                    .size(65.dp)
                    .background(
                        Color.Green
                    )
                    .clickable {
                        startButton(hash.value, prefix.value)
                    }
                    .padding(10.dp)

            ) {
                Text(
                    "START",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

    }
}