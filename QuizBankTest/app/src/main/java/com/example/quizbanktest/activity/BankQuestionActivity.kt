package com.example.quizbanktest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.quizbanktest.R

class BankQuestionActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var backArrowBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)

        val name = intent.getStringExtra("NAME")
        textView = findViewById(R.id.title)
        textView.setText(name)

        backArrowBtn = findViewById(R.id.btn_back_arrow)
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.question_to_bank_in, R.anim.question_to_bank_out)
    }
}