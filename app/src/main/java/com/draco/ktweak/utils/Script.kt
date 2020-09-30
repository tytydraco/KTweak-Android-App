package com.draco.ktweak.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import org.json.JSONArray
import java.io.File
import java.net.URL

class Script(private val context: Context) {
    companion object {
        const val scriptName = "script"
        const val logName = "log"

        class Commit {
            var message = ""
            var date = ""
            var url = ""
        }

        enum class ExecuteStatus {
            SUCCESS,
            FAILURE,
            MISSING
        }

        enum class UpdateStatus {
            SUCCESS,
            FAILURE,
            UNCHANGED
        }
    }

    private fun getGitApiJSON(url: String): JSONArray {
        val apiUrl = URL(url)
        var json: String

        /* If we can't make the connection, retry until we can */
        while (true) {
            try {
                json = apiUrl.readText()
                break
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Thread.sleep(1000)
        }

        return JSONArray(json)
    }

    fun commits(branch: String): List<Commit> {
        val gitAuthor = context.getString(R.string.git_author)
        val gitRepo = context.getString(R.string.git_repo)

        val jsonArray = getGitApiJSON("https://api.github.com/repos/$gitAuthor/$gitRepo/commits?sha=$branch")

        /* Parse returned JSON */
        val commits = arrayListOf<Commit>()
        for (i in 0 until jsonArray.length()) {
            val commit = Commit()
            with(commit) {
                try {
                    message = jsonArray
                        .getJSONObject(i)
                        .getJSONObject("commit")
                        .getString("message").lines()[0]
                    date = jsonArray
                        .getJSONObject(i)
                        .getJSONObject("commit")
                        .getJSONObject("author").getString("date")
                        .replace("T", "\n")
                        .replace("Z", "")
                    url = jsonArray
                        .getJSONObject(i)
                        .getString("html_url")
                    commits += commit
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return commits
    }

    fun branches(): List<String> {
        val gitAuthor = context.getString(R.string.git_author)
        val gitRepo = context.getString(R.string.git_repo)
        val apiUrl = "https://api.github.com/repos/$gitAuthor/$gitRepo/branches"
        val jsonArray = getGitApiJSON(apiUrl)

        /* Parse returned JSON */
        val branches = arrayListOf<String>()
        for (i in 0 until jsonArray.length()) {
            try {
               branches += jsonArray.getJSONObject(i).getString("name")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return branches
    }

    fun execute(): ExecuteStatus {
        val script = File(context.filesDir, scriptName)
        val log = File(context.filesDir, logName)

        /* Make sure script exists locally */
        if (!script.exists())
            return ExecuteStatus.MISSING

        /* Start in parsable mode */
        val process = ProcessBuilder("su", "-c", "sh", script.absolutePath)
            .redirectErrorStream(true)
            .start()

        process.waitFor()

        /* Write output to log file */
        log.writeText(process.inputStream.bufferedReader().readText())

        return if (process.exitValue() == 0)
            ExecuteStatus.SUCCESS
        else
            ExecuteStatus.FAILURE
    }

    fun update(): UpdateStatus {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!
        val script = File(context.filesDir, scriptName)

        val gitAuthor = context.getString(R.string.git_author)
        val gitRepo = context.getString(R.string.git_repo)
        val gitScriptPath = context.getString(R.string.git_script_path)
        val url = URL("https://raw.githubusercontent.com/$gitAuthor/$gitRepo/$branch/$gitScriptPath")

        val bytes = try {
            url.readBytes()
        } catch(e: Exception) {
            e.printStackTrace()
            return UpdateStatus.FAILURE
        }

        if (script.exists() && script.readBytes().contentEquals(bytes))
            return UpdateStatus.UNCHANGED

        script.writeBytes(bytes)
        return UpdateStatus.SUCCESS
    }
}