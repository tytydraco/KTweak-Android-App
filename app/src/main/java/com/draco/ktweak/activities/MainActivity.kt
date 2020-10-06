package com.draco.ktweak.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.draco.ktweak.R
import com.draco.ktweak.fragments.MainPreferenceFragment
import com.draco.ktweak.services.UpdateWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private fun isRooted(): Boolean {
        val rootCheckProcess = ProcessBuilder("su", "-c", "exit").start()
        rootCheckProcess.waitFor()
        return rootCheckProcess.exitValue() == 0
    }

    private fun setupUpdateWorker() {
        val updateWorkRequest =
            PeriodicWorkRequestBuilder<UpdateWorker>(1, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS).build()

        with (WorkManager.getInstance(this)) {
            cancelAllWork()
            enqueue(updateWorkRequest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Setup preference screen */
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_layout, MainPreferenceFragment())
            .commit()

        /* Warn user on each start if root is not detected */
        if (!isRooted()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_root_not_granted_title))
                .setMessage(getString(R.string.alert_root_not_granted_message))
                .setPositiveButton(getString(R.string.alert_confirm), null)
                .show()
        }

        setupUpdateWorker()
    }
}