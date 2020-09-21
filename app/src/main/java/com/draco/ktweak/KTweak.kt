package com.draco.ktweak

import android.content.Context
import java.io.File
import java.lang.Exception
import java.net.URL

class KTweak(private val context: Context) {
    companion object {
        const val scriptName = "ktweak"
        const val scriptURL = "https://raw.githubusercontent.com/tytydraco/ktweak/master/ktweak"
        const val changelogURL = "https://github.com/tytydraco/ktweak/commits/master/ktweak"
        const val logName = "log"

        enum class ExecuteStatus {
            SUCCESS,
            FAILURE,
            MISSING
        }
    }

    private fun getLatestScriptBytes(callback: (ByteArray) -> Unit) {
        val url = URL(scriptURL)
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
        val process = ProcessBuilder("su", "-c", "sh", script.absolutePath)
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