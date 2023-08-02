package com.example.quizbanktest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizbanktest.R

class BankSingleAnswerQuestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_single_answer_question)
    }
}