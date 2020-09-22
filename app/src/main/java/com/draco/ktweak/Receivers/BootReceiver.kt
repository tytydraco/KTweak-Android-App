package com.draco.ktweak.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.draco.ktweak.Services.BootService

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val bootServiceIntent = Intent(context, BootService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(bootServiceIntent)
            else
                context.startService(bootServiceIntent)
        }
    }
}