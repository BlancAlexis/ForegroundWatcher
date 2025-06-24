package fr.ablanc.foregroundwatcher.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.ablanc.foregroundwatcher.MainActivity.Companion.ServiceIntent

class StopServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(ServiceIntent)
    }
}