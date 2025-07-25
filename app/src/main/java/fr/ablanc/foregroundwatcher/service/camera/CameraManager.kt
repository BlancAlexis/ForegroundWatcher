package fr.ablanc.foregroundwatcher.service.camera

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val analyzer: ImageAnalysis.Analyzer
) {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var camera: Camera
    lateinit var videoCapture: VideoCapture<Recorder>

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()

            val analysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, analyzer)
                }

            val recorder =
                Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HD))
                    .setExecutor(executor)
                    .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            provider.unbindAll()
            camera = provider.bindToLifecycle(lifecycleOwner, selector, analysis, videoCapture)
        }, ContextCompat.getMainExecutor(context))
    }

    fun toggleFlash(enable: Boolean) {
        if (::camera.isInitialized) {
            camera.cameraControl.enableTorch(enable)
        }
    }

    fun shutdown() {
        executor.shutdown()
    }
}