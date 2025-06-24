package fr.ablanc.foregroundwatcher

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.ablanc.foregroundwatcher.service.MotionDetectionService
import kotlin.jvm.java

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.FOREGROUND_SERVICE_CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS
            ),
            0
        )
       ServiceIntent = Intent(this, MotionDetectionService::class.java)
        ContextCompat.startForegroundService(this, ServiceIntent)
    }

    companion object{
        lateinit var ServiceIntent : Intent
    }
}