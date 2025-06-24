package fr.ablanc.foregroundwatcher.util

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

object FileUtils {
    fun getOutputFile(): File {
        val mediaDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "MotionCam"
        )
        if (!mediaDir.exists()) mediaDir.mkdirs()
        return File(mediaDir, "${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(System.currentTimeMillis())}.mp4")
    }
}