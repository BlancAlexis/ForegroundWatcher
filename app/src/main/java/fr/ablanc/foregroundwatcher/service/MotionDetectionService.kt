package fr.ablanc.foregroundwatcher.service

import android.content.Context
import android.os.*
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import fr.ablanc.foregroundwatcher.notification.NotificationHelper
import fr.ablanc.foregroundwatcher.service.camera.CameraManager
import fr.ablanc.foregroundwatcher.service.camera.RecorderManager
import fr.ablanc.foregroundwatcher.service.detection.MotionAnalyzer
import fr.ablanc.foregroundwatcher.util.FileUtils.getOutputFile
import kotlinx.coroutines.*

class MotionDetectionService : LifecycleService() {

    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var recorderManager: RecorderManager
    private lateinit var cameraManager: CameraManager
    private val motionAnalyzer: MotionAnalyzer by lazy{
        delegateMotionAnalyzer()
    }

    override fun onCreate() {
        super.onCreate()
        // WakeLock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Motion::WakeLock")
        wakeLock.acquire()

        // Notification
        startForeground(1, NotificationHelper.createNotification(this))

        recorderManager = RecorderManager(this)
        cameraManager = CameraManager(
            context = this,
            lifecycleOwner = this,
            analyzer = motionAnalyzer
        )
        cameraManager.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()
        recorderManager.stopRecording()
        cameraManager.shutdown()
    }

    private fun delegateMotionAnalyzer(): MotionAnalyzer = MotionAnalyzer {
        Log.d("MotionService", "\uD83D\uDCA5 Mouvement détecté !")
        lifecycleScope.launch {
            delay(30_000)
            val file = getOutputFile()
            recorderManager.startRecording(
                outputFile = file,
                onStart = { Log.d("Recorder", "Start") },
                onFinish = { Log.d("Recorder", "Saved to: $it") }
            )
            cameraManager.toggleFlash(true)
            delay(60_000)
            recorderManager.stopRecording()
            cameraManager.toggleFlash(false)
            delay(5_000)

            motionAnalyzer.resetCooldown()

        }
    }

}