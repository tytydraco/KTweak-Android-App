package com.draco.ktweak.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import com.draco.ktweak.services.BootService

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            if (!prefs.getBoolean(context.getString(R.string.pref_apply_on_boot), true))
                return

            val bootServiceIntent = Intent(context, BootService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(bootServiceIntent)
            else
                context.startService(bootServiceIntent)
        }
    }
}