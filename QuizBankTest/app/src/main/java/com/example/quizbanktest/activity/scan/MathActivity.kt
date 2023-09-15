package com.example.quizbanktest.activity.scan

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.example.quizbanktest.R

class MathActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math)
        val webView: WebView = findViewById(R.id.webview)
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
                val clip = ClipData.newPlainText("copiedText", text)
                clipboard.setPrimaryClip(clip)
            }
        }

        webView.addJavascriptInterface(WebAppInterface(this), "Android")
    }
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.findViewById<TextView>(R.id.tv_progressbar_text).text=text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}