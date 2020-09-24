package com.draco.ktweak.Activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.draco.ktweak.Fragments.MainPreferenceFragment
import com.draco.ktweak.R
import com.draco.ktweak.Utils.Script
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val script = Script(this)

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

    private fun fetch() {
        val ret = script.fetch()
        runOnUiThread {
            when (ret) {
                Script.Companion.FetchStatus.SUCCESS -> {
                    Snackbar.make(
                        swipeRefreshLayout, getString(R.string.snackbar_fetch_success),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }

                Script.Companion.FetchStatus.FAILURE -> {
                    Snackbar.make(
                        swipeRefreshLayout, getString(R.string.snackbar_fetch_failure),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }

                Script.Companion.FetchStatus.UNCHANGED -> {
                    Snackbar.make(
                        swipeRefreshLayout, getString(R.string.snackbar_fetch_unchanged),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.main_layout)
        swipeRefreshLayout.setOnRefreshListener {
            Thread {
                fetch()
            }.start()
        }

        /* Setup preference screen */
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_layout, MainPreferenceFragment())
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
                .setTitle(getString(R.string.alert_root_not_granted_title))
                .setMessage(getString(R.string.alert_root_not_granted_message))
                .setPositiveButton(getString(R.string.alert_confirm), null)
                .show()
        }
    }
}