package com.draco.ktweak.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.BuildConfig
import com.draco.ktweak.R
import com.draco.ktweak.adapters.LogRecyclerAdapter
import com.draco.ktweak.utils.Script
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import java.io.File

class LogActivity: AppCompatActivity() {
    private lateinit var script: Script

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: MaterialTextView
    private lateinit var viewAdapter: LogRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        /* Initialize variables */
        script = Script(this)
        recyclerView = findViewById(R.id.recycler_view)
        empty = findViewById(R.id.empty)

        /* If log does not exist, show warning */
        val log = File(filesDir, script.logName())
        if (!log.exists()) {
            recyclerView.visibility = View.GONE
            empty.visibility = View.VISIBLE
            return
        }

        val logLines = log.readLines()
        viewAdapter = LogRecyclerAdapter(logLines)

        recyclerView.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logs, menu)

        /* Clear logs */
        menu.findItem(R.id.clear_logs).setOnMenuItemClickListener {
            val log = File(filesDir, script.logName())

            if (!log.exists()) {
                Snackbar.make(recyclerView, getString(R.string.snackbar_clear_logs_failure), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.snackbar_dismiss)) {}
                    .show()
            } else {
                log.delete()
                finish()
            }
            return@setOnMenuItemClickListener true
        }

        /* Share log file */
        menu.findItem(R.id.share).setOnMenuItemClickListener {
            val log = File(filesDir, script.logName())

            if (!log.exists()) {
                Snackbar.make(recyclerView, getString(R.string.snackbar_share_failure), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.snackbar_dismiss)) {}
                    .show()
            } else {
                try {
                    val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", log)
                    val intent = Intent(Intent.ACTION_SEND)
                    with (intent) {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "*/*"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Snackbar.make(recyclerView, getString(R.string.snackbar_intent_failed), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.snackbar_dismiss)) {}
                        .show()
                }
            }
            return@setOnMenuItemClickListener true
        }

        return super.onCreateOptionsMenu(menu)
    }
}