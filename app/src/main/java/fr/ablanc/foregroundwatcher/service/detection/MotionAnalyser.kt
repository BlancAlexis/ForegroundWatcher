package fr.ablanc.foregroundwatcher.service.detection

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlin.math.abs

class MotionAnalyzer(
    private val onMotionDetected: () -> Unit
) : ImageAnalysis.Analyzer {

    private var previousY: ByteArray? = null
    private var cooldown = false

    override fun analyze(image: ImageProxy) {
        val yPlane = image.planes[0].buffer
        val currentY = ByteArray(yPlane.remaining())
        yPlane.get(currentY)

        previousY?.let { prev ->
            var changedPixels = 0
            for (i in currentY.indices step 10) {
                val diff = (currentY[i].toInt() and 0xFF) - (prev[i].toInt() and 0xFF)
                if (abs(diff) > 30) changedPixels++
            }

            if (!cooldown && changedPixels > 1000) {
                cooldown = true
                Log.d("MotionAnalyzer", "💥 Mouvement détecté avec $changedPixels pixels changés.")
                onMotionDetected()
            }
        }

        previousY = currentY
        image.close()
    }

    fun resetCooldown() {
        cooldown = false
    }
}
