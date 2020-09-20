package com.draco.ktweak

import android.content.Context
import java.util.concurrent.atomic.AtomicBoolean

class KTweak(private val context: Context) {
    private val scriptName = "ktweak"

    fun execute(callback: (() -> Unit)? = null) {
        val scriptBytes = context.assets.open(scriptName).readBytes()
        val tempScript = createTempFile(scriptName)
        val logFile = createTempFile("log")
        tempScript.writeBytes(scriptBytes)

        val process = ProcessBuilder("su", "-c", "sh", tempScript.absolutePath)
            .redirectErrorStream(true)
            .start()

        Thread {
            process.waitFor()
            logFile.writeText(process.inputStream.bufferedReader().readText())
            if (callback != null) callback()
        }.start()
    }
}