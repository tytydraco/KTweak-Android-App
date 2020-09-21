package com.draco.ktweak

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class ChangelogActivity: AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar

    private class CustomWebViewClient(private val progress: ProgressBar): WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            progress.visibility = View.GONE
            view!!.visibility = View.VISIBLE
            super.onPageFinished(view, url)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)

        webView = findViewById(R.id.webView)
        progress = findViewById(R.id.progress)

        with (webView) {
            webViewClient = CustomWebViewClient(this@ChangelogActivity.progress)
            loadUrl(KTweak.changelogURL)
            setBackgroundColor(getColor(R.color.colorPrimaryDark))
        }
    }
}