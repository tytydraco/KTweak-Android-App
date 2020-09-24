package com.draco.ktweak.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import com.draco.ktweak.Utils.Script

class BootService: Service() {
    private val notificationId = 0
    private val notificationChannelId = "script-boot-notification"
    private val notificationChannelName = getString(R.string.boot_notification_channel_name)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean(getString(R.string.pref_apply_on_boot), true)) {
            val script = Script(this)

            Thread {
                script.fetch()
                script.execute()

                /* Show boot notification */
                if (prefs.getBoolean(getString(R.string.pref_boot_notification), true)) {
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notification = NotificationCompat
                        .Builder(this, notificationChannelId)
                        .setSmallIcon(R.drawable.ic_baseline_whatshot_24)
                        .setContentTitle(getString(R.string.boot_notification_title))
                        .setContentText(getString(R.string.boot_notification_text))
                        .build()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notificationChannel = NotificationChannel(notificationChannelId,
                            notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT)
                        notificationManager.createNotificationChannel(notificationChannel)
                    }

                    notificationManager.notify(notificationId, notification)
                }
            }.start()
        }
    }
}