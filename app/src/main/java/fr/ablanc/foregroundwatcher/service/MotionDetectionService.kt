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

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Motion::WakeLock")
        wakeLock.acquire()


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
            delay(TEMPO_DURATION)
            val file = getOutputFile()
                recorderManager.startRecording(
                    videoCapture = cameraManager.videoCapture,
                    outputFile = file,
                    onStart = { Log.d("Recorder", "▶ Enregistrement démarré") },
                    onFinish = { Log.d("Recorder", "\uD83D\uDCBE Enregistré à : $it") }
                )
            cameraManager.toggleFlash(true)
            delay(REC_DURATION)
            recorderManager.stopRecording()
            cameraManager.toggleFlash(false)
            delay(5_000)
            motionAnalyzer.resetCooldown()

        }
    }

    companion object {
        const val REC_DURATION = 10_000L
        const val TEMPO_DURATION = 5_000L
    }

}