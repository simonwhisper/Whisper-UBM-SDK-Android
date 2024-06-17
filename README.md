-- WHISPER UBM ANDROID SDK README --

To add whisper ubm to your android studio project

1.create libs folder if you dont already have one myproject/app/libs

2.add WhisperUBM.arr to libs

3.add WhisperUBM and dependencies to gradel

 	implementation(files("libs/WhisperUBM.aar"))
    implementation("com.google.android.gms:play-services-ads-identifier:18.1.0")
    implementation("com.azure:azure-messaging-eventhubs:5.18.0")
    implementation("com.azure:azure-identity:1.11.2")

4.also add the following inside the android{} section of your gradel file

	packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,INDEX.LIST,io.netty.versions.properties}"
        }
    }

4.add RECORD_AUDIO permission to manifest and add a feture to reuest permissison from the userr in app

5.add the code "import com.whisper.whisperubmandroid.WhisperUBM" to the activity where you wish to implement whisper