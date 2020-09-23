package com.draco.ktweak.Utils

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

        enum class ExecuteStatus {
            SUCCESS,
            FAILURE,
            MISSING
        }

        enum class FetchStatus {
            SUCCESS,
            FAILURE,
            UNCHANGED
        }
    }

    private fun getLatestScriptBytes(branch: String): ByteArray? {
        val gitAuthor = context.getString(R.string.git_author)
        val gitRepo = context.getString(R.string.git_repo)
        val gitScriptPath = context.getString(R.string.git_script_path)
        val url = URL("https://raw.githubusercontent.com/" +
                "$gitAuthor/$gitRepo/$branch/$gitScriptPath")
        return try {
            url.readBytes()
        } catch(e: Exception) {
            null
        }
    }

    fun listBranches(): List<String> {
        val gitAuthor = context.getString(R.string.git_author)
        val gitRepo = context.getString(R.string.git_repo)

        val commitsURL = URL("https://api.github.com/repos/$gitAuthor/$gitRepo/branches")
        var json: String

        /* If we can't make the connection, retry until we can */
        while (true) {
            try {
                json = commitsURL.readText()
                break
            } catch (_: Exception) {}
            Thread.sleep(1000)
        }

        val jsonArray = JSONArray(json)
        val branches = arrayListOf<String>()

        /* Parse returned JSON */
        for (i in 0 until jsonArray.length()) {
            try {
                val branch = jsonArray.getJSONObject(i).getString("name")

                /* If script does not exist on remote branch, skip */
                if (getLatestScriptBytes(branch) != null)
                    branches += branch
            } catch (_: Exception) {}
        }

        return branches
    }

    fun fetch(): FetchStatus {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!
        val script = File(context.filesDir, scriptName)
        val bytes = getLatestScriptBytes(branch) ?: return FetchStatus.FAILURE

        if (script.exists() && script.readBytes().contentEquals(bytes))
            return FetchStatus.UNCHANGED

        script.writeBytes(bytes)
        return FetchStatus.SUCCESS
    }

    fun execute(): ExecuteStatus {
        val script = File(context.filesDir, scriptName)
        val log = File(context.filesDir, logName)

        /* Make sure script exists locally */
        if (!script.exists())
            return ExecuteStatus.MISSING

        /* Start in parsable mode */
        val process = ProcessBuilder("su", "-c", "sh",
            script.absolutePath, "-p")
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
}