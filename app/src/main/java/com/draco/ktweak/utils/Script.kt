package com.draco.ktweak.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import java.io.File
import java.net.URL

class Script(private val context: Context) {
    companion object {
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

    private val gitAuthor = context.getString(R.string.git_author)
    private val gitRepo = context.getString(R.string.git_repo)

    fun scriptName(): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!
        return "script-$branch.sh"
    }

    fun logName(): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!
        return "log-$branch.txt"
    }

    fun execute(): ExecuteStatus {
        val script = File(context.filesDir, scriptName())
        val log = File(context.filesDir, logName())

        /* Make sure script exists locally */
        if (!script.exists())
            return ExecuteStatus.MISSING

        /* Start the process */
        try {
            val process = ProcessBuilder("su", "-c", "sh ${script.absolutePath}")
                .redirectErrorStream(true)
                .start()

            process.waitFor()

            /* Write output to log file */
            log.writeText(process.inputStream.bufferedReader().readText())

            return if (process.exitValue() == 0)
                ExecuteStatus.SUCCESS
            else
                ExecuteStatus.FAILURE
        } catch (_: Exception) {
            return ExecuteStatus.FAILURE
        }
    }

    fun update(): UpdateStatus {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!
        val script = File(context.filesDir, scriptName())

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