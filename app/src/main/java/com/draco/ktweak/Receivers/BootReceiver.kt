package com.draco.ktweak.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import com.draco.ktweak.Utils.KTweak

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            if (prefs.getBoolean(context.getString(R.string.pref_apply_on_boot), true)) {
                val ktweak = KTweak(context)
                val autoFetch = prefs.getBoolean(context.getString(R.string.pref_auto_fetch), true)
                if (autoFetch)
                    ktweak.updateScript { ktweak.execute() }
                else
                    ktweak.execute()
            }
        }
    }
}