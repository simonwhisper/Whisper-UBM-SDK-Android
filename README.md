-- WHISPER UBM ANDROID SDK README --

To add whisper ubm to your android studio project

1. Create libs folder if you dont already have one myproject/app/libs

2. Add WhisperUBM.arr to libs (you can find this at /WhisperSDK_DemoApp/app/libs)

3. add WhisperUBM and dependencies to gradle

 	implementation(files("libs/WhisperUBM.aar"))
    implementation("com.google.android.gms:play-services-ads-identifier:18.1.0")
    implementation("com.azure:azure-messaging-eventhubs:5.18.0")
    implementation("com.azure:azure-identity:1.11.2")

4. Also add the following inside the android{} section of your gradle file

	packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,INDEX.LIST,io.netty.versions.properties}"
        }
    }

4. Add RECORD_AUDIO permission to manifest and add a feature to request permissison from the user in app

5. Add the code "import com.whisper.whisperubmandroid.WhisperUBM" to the activity where you wish to implement whisper