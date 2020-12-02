package com.draco.ktweak.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.draco.ktweak.R
import com.draco.ktweak.fragments.MainPreferenceFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private fun isRooted(): Boolean {
        return try {
            val rootCheckProcess = ProcessBuilder("su", "-c", "exit").start()
            rootCheckProcess.waitFor()
            rootCheckProcess.exitValue() == 0
        } catch (_: Exception) {
            false
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
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.alert_root_not_granted_title))
                .setMessage(getString(R.string.alert_root_not_granted_message))
                .setPositiveButton(getString(R.string.alert_confirm), null)
                .show()
        }
    }
}