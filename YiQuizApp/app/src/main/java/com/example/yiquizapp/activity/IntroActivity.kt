package com.example.yiquizapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.yiquizapp.utils.ConstantsAccountServiceFunction
import com.example.yiquizapp.R

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val signIn : TextView = findViewById(R.id.sign_in_intro)
        signIn.setOnClickListener {
            ConstantsAccountServiceFunction.getCsrfToken(this)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}