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

    private fun getLatestScriptBytes(callback: (ByteArray) -> Unit) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!

        val url = URL("https://raw.githubusercontent.com/tytydraco/KTweak/$branch/ktweak")
        var scriptBytes = byteArrayOf()
        Thread {
            try {
                scriptBytes = url.readBytes()
            } catch(e: Exception) {}
            callback(scriptBytes)
        }.start()
    }

    fun updateScript(callback: ((FetchStatus) -> Unit)? = null) {
        val script = File(context.filesDir, scriptName)
        getLatestScriptBytes {
            if (it.isEmpty()) {
                if (callback != null) callback(FetchStatus.FAILURE)
                return@getLatestScriptBytes
            }

            if (script.exists() && script.readBytes().contentEquals(it)) {
                if (callback != null) callback(FetchStatus.UNCHANGED)
                return@getLatestScriptBytes
            }

            script.writeBytes(it)
            if (callback != null) callback(FetchStatus.SUCCESS)
        }
    }

    private fun runScript(): ExecuteStatus {
        val script = File(context.filesDir, scriptName)
        val log = File(context.filesDir, logName)

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

    fun execute(callback: ((ExecuteStatus) -> Unit)? = null) {
        val script = File(context.filesDir, scriptName)

        /* Make sure script exists locally */
        if (!script.exists()) {
            if (callback != null) callback(ExecuteStatus.MISSING)
            return
        }

        /* Execute async */
        Thread {
            val ret = runScript()
            if (callback != null) callback(ret)
        }.start()
    }
}