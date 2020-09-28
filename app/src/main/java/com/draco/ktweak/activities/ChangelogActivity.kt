package com.draco.ktweak.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.adapters.ChangelogRecyclerAdapter
import com.draco.ktweak.R
import com.draco.ktweak.utils.Script

class ChangelogActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var viewAdapter: ChangelogRecyclerAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        val script = Script(this)
        recyclerView = findViewById(R.id.recycler_view)
        progress = findViewById(R.id.progress)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val branch = prefs.getString(getString(R.string.pref_branch), "master")!!

        /* Fetch commits from GitHub using public API */
        Thread {
            val commits = script.commits(branch)
            /* Update recycler */
            runOnUiThread {
                /* Hide progress bar */
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView = findViewById(R.id.recycler_view)
                viewAdapter = ChangelogRecyclerAdapter(this, commits)
                recyclerView.apply {
                    adapter = viewAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL))
                }
                viewAdapter.notifyDataSetChanged()
            }
        }.start()
    }
}