package com.example.yiquizapp.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.yiquizapp.R


class BankMainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var backArrowBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_main)

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
        overridePendingTransition(R.anim.bank_to_main_in, R.anim.bank_to_main_out)
    }


}