package com.draco.ktweak

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LogActivity: AppCompatActivity() {
    private lateinit var ktweak: KTweak

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: TextView
    private lateinit var viewAdapter: LogRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        /* Initialize variables */
        ktweak = KTweak(this)
        recyclerView = findViewById(R.id.recycler_view)
        empty = findViewById(R.id.empty)

        val log = File(filesDir, KTweak.logName)

        if (!log.exists()) {
            recyclerView.visibility = View.GONE
            empty.visibility = View.VISIBLE
            return
        }

        val logLines = log.readLines()
        viewAdapter = LogRecyclerAdapter(this, logLines)

        recyclerView.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewAdapter.notifyDataSetChanged()
    }
}