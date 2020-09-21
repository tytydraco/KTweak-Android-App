package com.draco.ktweak

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

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