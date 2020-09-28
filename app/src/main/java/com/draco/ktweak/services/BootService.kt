package com.draco.ktweak.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.draco.ktweak.utils.Script

class BootService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Script(this).execute()
        return super.onStartCommand(intent, flags, startId)
    }
}