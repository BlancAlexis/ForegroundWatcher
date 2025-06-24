package fr.ablanc.foregroundwatcher.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import fr.ablanc.foregroundwatcher.R
import fr.ablanc.foregroundwatcher.service.MotionDetectionService
import kotlin.jvm.java

object NotificationHelper {

    fun createNotification(context: Context): Notification {

        val stopIntent = Intent(context, StopServiceReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val channelId = "motion_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Détection de Mouvement", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification permanente de détection de mouvement"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Détection active")
            .setContentText("Surveillance en cours")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_background, "Stop", stopPendingIntent)
            .build()
    }
}
