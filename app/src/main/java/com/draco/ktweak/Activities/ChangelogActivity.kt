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
import java.net.URL

class ChangelogActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var viewAdapter: ChangelogRecyclerAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        recyclerView = findViewById(R.id.recycler_view)
        progress = findViewById(R.id.progress)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        /* Fetch commits from GitHub using public API */
        Thread {
            val branch = prefs.getString(getString(R.string.pref_branch), "master")!!
            val commitsURL = URL("https://api.github.com/repos/" +
                    "${getString(R.string.git_author)}/${getString(R.string.git_repo)}/commits?sha=$branch")
            var json: String

            /* If we can't make the connection, retry until we can */
            while (true) {
                try {
                    json = commitsURL.readText()
                    break
                } catch (_: Exception) {}
            }

            val jsonArray = JSONArray(json)
            val changelogItems = arrayListOf<ChangelogItem>()

            /* Parse returned JSON */
            for (i in 0 until jsonArray.length()) {
                val changelogItem = ChangelogItem()
                with(changelogItem) {
                    try {
                        message = jsonArray.getJSONObject(i).getJSONObject("commit")
                            .getString("message").lines()[0]
                        date = jsonArray.getJSONObject(i).getJSONObject("commit")
                            .getJSONObject("author").getString("date")
                            .replace("T", "\n")
                            .replace("Z", "")
                        url = jsonArray.getJSONObject(i).getString("html_url")
                        changelogItems += changelogItem
                    } catch (_: Exception) {}
                }
            }

            /* Update recycler */
            runOnUiThread {
                /* Hide progress bar */
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView = findViewById(R.id.recycler_view)
                viewAdapter = ChangelogRecyclerAdapter(this, changelogItems)
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