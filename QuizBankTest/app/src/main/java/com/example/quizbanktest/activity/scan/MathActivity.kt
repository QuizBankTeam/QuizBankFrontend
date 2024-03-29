package com.example.quizbanktest.activity.scan

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity


class MathActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)
        val webView: WebView = findViewById(R.id.webview)
        val intent = intent
        var latex = intent.getStringExtra("latex")
        if(latex == null) latex = "null"

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            // 當網頁開始加載時
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgressDialog("目前在渲染中請稍等")
            }

            // 當網頁加載完成時
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideProgressDialog()
            }
        }
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
                Toast.makeText(this@MathActivity,"已經copy對應之latex語法 $text",Toast.LENGTH_SHORT).show()
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("copiedText", "\\("+escapeSpecialCharacters(text)+"\\)")
                clipboard.setPrimaryClip(clip)
            }

            @JavascriptInterface
            fun getExtra(): String {
                return unprocessLatex(latex)
            }

        }

        webView.addJavascriptInterface(WebAppInterface(this), "Android")
    }
    fun escapeSpecialCharacters(input: String): String {
        // 先轉換 \\ 至 \\\\，再轉換 \ 至 \\，然後轉換 & 至 &amp;
        return input.replace("&", "&amp;")
    }
    fun unprocessLatex(input: String): String {
        // 移除 `\\(` 和 `\\)`
        var result = input.trim()
        if (result.startsWith("\\(") && result.endsWith("\\)")) {
            result = result.drop(2).dropLast(2)
        }

        // 還原 &amp; 至 &
        result = result.replace("&amp;", "&")

        return result
    }

}