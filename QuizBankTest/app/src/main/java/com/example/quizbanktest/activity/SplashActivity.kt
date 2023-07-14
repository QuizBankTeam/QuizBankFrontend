package com.example.quizbanktest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.quizbanktest.R
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_splash)
        ConstantsAccountServiceFunction.getCsrfToken(this)
        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            ConstantsAccountServiceFunction.getCsrfToken(this@SplashActivity)

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
}