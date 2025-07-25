package fr.ablanc.foregroundwatcher.service.camera

import android.content.Context
import android.net.Uri
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors

class RecorderManager(private val context: Context) {

    private var recording: Recording? = null

    fun startRecording(
        videoCapture: VideoCapture<Recorder>,
        outputFile: File,
        onStart: () -> Unit,
        onFinish: (Uri?) -> Unit
    ) {
        val outputOptions = FileOutputOptions.Builder(outputFile).build()
        recording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> onStart()
                    is VideoRecordEvent.Finalize -> onFinish(event.outputResults.outputUri)
                }
            }
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }
}
