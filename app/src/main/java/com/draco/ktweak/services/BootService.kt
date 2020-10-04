package com.draco.ktweak.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.draco.ktweak.R
import com.draco.ktweak.utils.Script

class BootService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /* Create boot notification */
        val notificationId = 1
        val notificationChannelId = getString(R.string.boot_notification_channel_id)
        val notificationChannelName = getString(R.string.boot_notification_channel_name)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val notification = NotificationCompat
            .Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.ic_baseline_whatshot_24)
            .setContentTitle(getString(R.string.boot_notification_title))
            .setContentText(getString(R.string.boot_notification_text))
            .setOngoing(true)
            .build()

        /* We only need the notification channel for newer versions */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        /* Start notification */
        startForeground(notificationId, notification)
        Script(this).execute()
        stopForeground(true)
        stopSelf()

        return super.onStartCommand(intent, flags, startId)
    }
}