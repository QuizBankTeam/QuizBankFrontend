package com.example.quizbanktest.activity.scan

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.quizbanktest.R

class MathActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)
        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        webView.loadUrl("file:///android_asset/math_keyboard.html")
        webView.evaluateJavascript("yourJavaScriptFunction();", null)
        webView.settings.javaScriptEnabled = true
        class WebAppInterface(private val activity: Activity) {
            @JavascriptInterface
            fun closeActivity() {
                activity.finish()
            }
            @JavascriptInterface
            fun copyToClipboard(text: String) {
                Log.e("math","copy")
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("copiedText", text)
                clipboard.setPrimaryClip(clip)
            }
        }

        webView.addJavascriptInterface(WebAppInterface(this), "Android")
    }
}