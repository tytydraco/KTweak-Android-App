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

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_layout, MainPreferenceFragment())
            .commit()

        if (!isRooted()) {
            AlertDialog.Builder(this)
                .setTitle("Not Rooted")
                .setMessage("Superuser binary was not detected. Ensure your device is rooted.")
                .setPositiveButton("Okay", null)
                .show()
        } else if (!rootGranted()) {
            AlertDialog.Builder(this)
                .setTitle("Root Not Granted")
                .setMessage("Superuser permissions are not enabled for this app. Some functionality will be restricted.")
                .setPositiveButton("Okay", null)
                .show()
        }
    }
}