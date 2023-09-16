package com.example.quizbanktest.activity.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.IntroActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val backBtn : ImageButton = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish() }
        val signUpBtn : android.widget.Button = findViewById(R.id.signUpbutton)
        signUpBtn.setOnClickListener {

        }
    }
}