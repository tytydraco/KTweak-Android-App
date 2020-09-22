package com.draco.ktweak.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import com.draco.ktweak.Utils.KTweak

class BootService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean(getString(R.string.pref_apply_on_boot), true)) {
            val ktweak = KTweak(this)
            val autoFetch = prefs.getBoolean(getString(R.string.pref_auto_fetch), true)

            Thread {
                if (autoFetch)
                    ktweak.fetch()

                ktweak.execute()
            }.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}