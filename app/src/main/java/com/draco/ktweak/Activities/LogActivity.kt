package com.draco.ktweak.Activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.Utils.Script
import com.draco.ktweak.Adapters.LogRecyclerAdapter
import com.draco.ktweak.R
import com.google.android.material.snackbar.Snackbar
import java.io.File

class LogActivity: AppCompatActivity() {
    private lateinit var script: Script

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: TextView
    private lateinit var viewAdapter: LogRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        /* Initialize variables */
        script = Script(this)
        recyclerView = findViewById(R.id.recycler_view)
        empty = findViewById(R.id.empty)

        /* If log does not exist, show warning */
        val log = File(filesDir, Script.logName)
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

        viewAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logs, menu)

        /* Clear logs */
        menu!!.findItem(R.id.clear_logs).setOnMenuItemClickListener {
            val log = File(filesDir, Script.logName)

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

        return super.onCreateOptionsMenu(menu)
    }
}