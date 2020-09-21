package com.draco.ktweak

import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.io.File
import java.lang.Exception
import java.net.URL

class KTweak(private val context: Context) {
    companion object {
        const val scriptName = "ktweak"
        const val scriptURL = "https://raw.githubusercontent.com/tytydraco/KTweak/master/ktweak"
        const val changelogURL = "https://github.com/tytydraco/KTweak/commits/master/ktweak"
        const val logName = "log"

        enum class ExecuteStatus {
            SUCCESS,
            FAILURE,
            MISSING
        }
    }

    private fun getLatestScriptBytes(callback: (ByteArray) -> Unit) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val branch = prefs.getString(context.getString(R.string.pref_branch), "master")!!

        val url = URL(scriptURL.replace("master", branch))
        var scriptBytes = byteArrayOf()
        Thread {
            try {
                val connection = url.openConnection()
                scriptBytes = connection.getInputStream().readBytes()
            } catch(e: Exception) {}
            callback(scriptBytes)
        }.start()
    }

    private fun runScript(): ExecuteStatus {
        val script = File(context.filesDir, scriptName)
        val log = File(context.filesDir, logName)
        val process = ProcessBuilder("su", "-c", "sh", script.absolutePath, "-p")
            .redirectErrorStream(true)
            .start()

        process.waitFor()
        log.writeText(process.inputStream.bufferedReader().readText())
        return if (process.exitValue() == 0)
            ExecuteStatus.SUCCESS
        else
            ExecuteStatus.FAILURE
    }

    fun execute(fetch: Boolean = true, callback: ((ExecuteStatus) -> Unit)? = null) {
        val script = File(context.filesDir, scriptName)

        if (fetch) {
            getLatestScriptBytes {
                var bytes = it

                if (bytes.isEmpty() && script.exists())
                    bytes = script.readBytes()

                if (bytes.isEmpty()) {
                    if (callback != null) callback(ExecuteStatus.MISSING)
                    return@getLatestScriptBytes
                }

                script.writeBytes(bytes)

                Thread {
                    val ret = runScript()
                    if (callback != null) callback(ret)
                }.start()
            }
        } else {
            Thread {
                var bytes = byteArrayOf()

                if (script.exists())
                    bytes = script.readBytes()

                if (bytes.isEmpty()) {
                    if (callback != null) callback(ExecuteStatus.MISSING)
                    return@Thread
                }

                val ret = runScript()
                if (callback != null) callback(ret)
            }.start()
        }
    }
}