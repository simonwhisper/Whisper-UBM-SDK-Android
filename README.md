-- WHISPER UBM ANDROID SDK README --

To add whisper ubm to your android studio project

1. Create libs folder if you don't already have one myproject/app/libs

2. Add WhisperUBM.arr to libs (you can find this at /WhisperSDK_DemoApp/app/libs)

3. add WhisperUBM and dependencies to gradle

 	implementation(files("libs/WhisperUBM.aar"))

4. Add RECORD_AUDIO permission to manifest and add a feature to request permission from the user in app

5. Add the code "import com.whisper.whisperubmandroid.WhisperUBM" to the activity where you wish to implement whisper