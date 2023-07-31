package com.example.quizbanktest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import com.example.quizbanktest.R
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import java.io.File

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            val data = readFile("loginSuccess.txt") //用來判斷之前的登入紀錄以便幫使用者自動登入
            Log.d("Data:", data)

            if (true) { //TODO 用於紀錄使用者是否登入過
                // Start the Main Activity
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))

            } else {
                // Start the Intro Activity
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish() // Call this when your activity is done and should be closed.
        }, 2500) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }

    fun readFile(filename: String): String { //在login頁面登入成功會自動寫入系統的快取
        val cacheDir = externalCacheDir?.absoluteFile.toString()
        val file = File(cacheDir, filename)
        if (!file.exists()) {
            Log.e("Error:", "File does not exist")
            return ""
        }
        return file.readText()
    }

}