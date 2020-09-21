package com.draco.ktweak.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.Adapters.ChangelogRecyclerAdapter
import com.draco.ktweak.R
import org.json.JSONArray
import java.net.URL

class ChangelogActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ChangelogRecyclerAdapter

    private fun getChangelog(callback: (ArrayList<String>) -> Unit) {
        Thread {
            val json = URL("https://api.github.com/repos/tytydraco/KTweak/commits").readText()
            val jsonArray = JSONArray(json)
            val messages = arrayListOf<String>()

            for (i in 0 until jsonArray.length()) {
                messages += jsonArray.getJSONObject(i).getJSONObject("commit").getString("message").lines()[0]
            }

            callback(messages)
        }.start()
    }

    private fun setupRecycler() {
        getChangelog {
            runOnUiThread {
                recyclerView = findViewById(R.id.recycler_view)
                viewAdapter = ChangelogRecyclerAdapter(it)
                recyclerView.apply {
                    adapter = viewAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                }
                viewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        recyclerView = findViewById(R.id.recycler_view)
        viewAdapter = ChangelogRecyclerAdapter(arrayListOf())
        recyclerView.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        viewAdapter.notifyDataSetChanged()
        setupRecycler()
    }
}