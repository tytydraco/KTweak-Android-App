package com.draco.ktweak.Utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.draco.ktweak.R
import java.io.File
import java.net.URL

class KTweak(private val context: Context) {
    companion object {
        const val scriptName = "ktweak"
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

    private fun getLatestScriptBytes(): ByteArray? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!

        val url = URL("https://raw.githubusercontent.com/tytydraco/KTweak/$branch/ktweak")
        return try {
            url.readBytes()
        } catch(e: Exception) {
            null
        }
    }

    fun fetch(): FetchStatus {
        val script = File(context.filesDir, scriptName)
        val bytes = getLatestScriptBytes() ?: return FetchStatus.FAILURE

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
        val process = ProcessBuilder("su", "-c", "sh", script.absolutePath, "-p")
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