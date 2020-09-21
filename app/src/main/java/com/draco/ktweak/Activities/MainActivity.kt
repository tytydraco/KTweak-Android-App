package com.draco.ktweak.Activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.draco.ktweak.Fragments.MainPreferenceFragment
import com.draco.ktweak.R

class MainActivity : AppCompatActivity() {
    private fun isRooted(): Boolean {
        val rootCheckProcess = ProcessBuilder("which", "su").start()
        rootCheckProcess.waitFor()
        return rootCheckProcess.exitValue() == 0
    }

    private fun rootGranted(): Boolean {
        val rootCheckProcess = ProcessBuilder("su", "-c", "exit").start()
        rootCheckProcess.waitFor()
        return rootCheckProcess.exitValue() == 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Setup preference screen */
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_layout, MainPreferenceFragment())
            .commit()

        /* Warn user on each start if root is not detected */
        if (!isRooted()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_not_rooted_title))
                .setMessage(getString(R.string.alert_not_rooted_message))
                .setPositiveButton(getString(R.string.alert_confirm), null)
                .show()
        } else if (!rootGranted()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_root_not_granted_message))
                .setMessage(getString(R.string.alert_root_not_granted_title))
                .setPositiveButton(getString(R.string.alert_confirm), null)
                .show()
        }
    }
}