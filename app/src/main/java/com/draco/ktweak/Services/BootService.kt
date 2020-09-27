package com.draco.ktweak.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.draco.ktweak.Utils.Script

class BootService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val script = Script(this)

        /* Pause the current thread until the fetch completes */
        val fetchThread = Thread { script.fetch() }
        with (fetchThread) {
            start()
            join()
        }

        /* Execute the script */
        script.execute()

        return super.onStartCommand(intent, flags, startId)
    }
}