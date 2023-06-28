package com.example.test.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.Adapter.QuestionAdapter
import com.example.test.databinding.SingleQuizSettingBinding

class SingleQuizSetting: AppCompatActivity() {
    private lateinit var quizSetAttrBinding: SingleQuizSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizSetAttrBinding = SingleQuizSettingBinding.inflate(layoutInflater)
        setContentView(quizSetAttrBinding.root)
//        fragM = supportFragmentManager
        init()

    }
    private fun init(){
        val intent = Intent()

    }
}