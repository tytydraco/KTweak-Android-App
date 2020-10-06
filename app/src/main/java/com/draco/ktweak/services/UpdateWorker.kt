package com.draco.ktweak.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.draco.ktweak.R
import com.draco.ktweak.utils.Script

class UpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
): Worker(appContext, workerParams) {
    private fun showNotification() {
        /* Create updated notification */
        val notificationId = 2
        val notificationChannelId = appContext.getString(R.string.update_notification_channel_id)
        val notificationChannelName = appContext.getString(R.string.update_notification_channel_name)
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val notification = NotificationCompat
            .Builder(appContext, notificationChannelId)
            .setSmallIcon(R.drawable.ic_baseline_whatshot_24)
            .setContentTitle(appContext.getString(R.string.update_notification_title))
            .setContentText(appContext.getString(R.string.update_notification_text))
            .build()

        /* We only need the notification channel for newer versions */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        /* Start notification */
        notificationManager.notify(notificationId, notification)
    }

    override fun doWork(): Result {
        return when (Script(appContext).update()) {
            /* Fetch failed, try again when possible */
            Script.Companion.UpdateStatus.FAILURE -> Result.retry()

            /* Nothing new, ignore */
            Script.Companion.UpdateStatus.UNCHANGED -> Result.success()

            /* Successfully updated, notify and back out */
            Script.Companion.UpdateStatus.SUCCESS -> {
                showNotification()
                Result.success()
            }
        }
    }
}