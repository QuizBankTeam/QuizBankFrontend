package com.example.quizbanktest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.quizbanktest.R
import com.example.quizbanktest.utils.ConstantsServiceFunction

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        var signIn : TextView = findViewById(R.id.sign_in_intro)
        signIn.setOnClickListener {
            ConstantsServiceFunction.getCsrfToken(this)
            ConstantsServiceFunction.login(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}