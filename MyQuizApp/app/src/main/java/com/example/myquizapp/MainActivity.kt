package com.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart: Button = findViewById(R.id.btn_start)
        val userName: EditText = findViewById(R.id.userName)

        btnStart.setOnClickListener {
            if(userName.text.isEmpty()){
                print(userName.text)
                Toast.makeText(this,"請輸入你的名字",Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(this,QuizQuestionActivity::class.java)
                intent.putExtra(Constants.USER_NAME,userName.text.toString())

                startActivity(intent)
                finish()
            }
        }

    }
}