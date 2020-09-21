package com.draco.ktweak.Activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.ktweak.Adapters.ChangelogRecyclerAdapter
import com.draco.ktweak.R
import com.draco.ktweak.Utils.ChangelogItem
import org.json.JSONArray
import java.lang.Exception
import java.net.URL

class ChangelogActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var viewAdapter: ChangelogRecyclerAdapter
    private lateinit var prefs: SharedPreferences

    private fun getChangelog(callback: (ArrayList<ChangelogItem>) -> Unit) {
        Thread {
            val branch = prefs.getString(getString(R.string.pref_branch), "master")!!
            val json = URL("https://api.github.com/repos/tytydraco/KTweak/commits?sha=$branch").readText()
            val jsonArray = JSONArray(json)
            val changelogItems = arrayListOf<ChangelogItem>()

            for (i in 0 until jsonArray.length()) {
                val changelogItem = ChangelogItem()
                with(changelogItem) {
                    try {
                        message = jsonArray.getJSONObject(i).getJSONObject("commit").getString("message").lines()[0]
                        date = jsonArray.getJSONObject(i).getJSONObject("commit").getJSONObject("author").getString("date")
                        url = jsonArray.getJSONObject(i).getString("html_url")
                    } catch (_: Exception) {}
                }
                changelogItems += changelogItem
            }

            callback(changelogItems)
        }.start()
    }

    private fun setupRecycler() {
        getChangelog {
            runOnUiThread {
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView = findViewById(R.id.recycler_view)
                viewAdapter = ChangelogRecyclerAdapter(this, it)
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
        progress = findViewById(R.id.progress)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        setupRecycler()
    }
}