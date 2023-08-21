package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import com.example.quizbanktest.R
import com.google.android.material.card.MaterialCardView

class BankQuestionSettingActivity : AppCompatActivity() {

    private lateinit var etQuestionTitle: EditText
    private lateinit var cardQuestionTitle: MaterialCardView
    private lateinit var unmodifiedText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_setting)

        cardQuestionTitle= findViewById(R.id.card_question_title)

        etQuestionTitle = findViewById(R.id.et_question_title)
        etQuestionTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  // p0:

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                unmodifiedText = s.toString()
                Log.e("BankQuestionSetting", "before s = $s")
                cardQuestionTitle.strokeColor = getColor(R.color.light_blue1)
                Log.e("BankQuestionSetting", "count = $count")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.e("BankQuestionSetting", "after s = $s")
                if (unmodifiedText != s.toString()) { // text changed
                    cardQuestionTitle.strokeColor = getColor(R.color.green)
                }
            }
        })

        pullExit()
    }

    private fun modifySetting() {
        //TODO: put new data to database
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun pullExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                doubleBackToExit()
            }
        }
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

}