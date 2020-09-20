package com.draco.ktweak

import android.content.Context
import java.util.concurrent.atomic.AtomicBoolean

class KTweak(private val context: Context) {
    private val scriptName = "ktweak"
    val executing = AtomicBoolean(false)
    var latestLog = String()

    fun execute(callback: (() -> Unit)? = null) {
        val scriptBytes = context.assets.open(scriptName).readBytes()
        val tempFile = createTempFile(scriptName)
        tempFile.writeBytes(scriptBytes)

        val process = ProcessBuilder("su", "-c", "sh", tempFile.absolutePath)
            .redirectErrorStream(true)
            .start()

        Thread {
            executing.set(true)
            process.waitFor()
            executing.set(false)

            latestLog = process.inputStream.bufferedReader().readText()

            if (callback != null) callback()
        }.start()
    }
}