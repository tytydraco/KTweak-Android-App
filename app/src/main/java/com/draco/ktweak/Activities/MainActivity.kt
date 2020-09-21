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
                .setCancelable(false)
                .setPositiveButton("Exit") { _, _ -> finish() }
                .show()
        }
    }
}