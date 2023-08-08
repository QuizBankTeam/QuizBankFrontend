package com.example.quizbanktest.activity.bank
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.group.GroupListActivity

import com.example.quizbanktest.adapters.bank.QuestionOptionsRecyclerViewAdapter

class BankSingleAnswerQuestionActivity : AppCompatActivity() {

    private lateinit var tvTitle : TextView
    private lateinit var tvType : TextView
    private lateinit var tvDescription : TextView
    private lateinit var btnBackArrow : ImageButton
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_single_answer_question)

        val questionId = intent.getStringExtra("id").toString()
        val questionTitle = intent.getStringExtra("title").toString()
        val questionNumber = intent.getStringExtra("number").toString()
        val questionDescription = intent.getStringExtra("description").toString()
        val questionOptions = intent.getStringArrayListExtra("options")
        val questionType = intent.getStringExtra("type").toString()
        val answerOptions = intent.getStringArrayListExtra("answerOptions")
        val answerDescription = intent.getStringExtra("answerDescription").toString()
        val questionSource = intent.getStringExtra("source").toString()
        val questionImage = intent.getStringArrayListExtra("image")
        val questionTag = intent.getStringArrayListExtra("tag")

        Log.d("BankSingleAnswerQuestionActivity", questionOptions.toString())
        Log.d("BankSingleAnswerQuestionActivity", answerOptions.toString())
        Log.d("BankSingleAnswerQuestionActivity", questionTag.toString())

        tvTitle = findViewById(R.id.question_title)
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)

        tvTitle.text = questionTitle
        tvType.text = questionType
        tvDescription.text = questionDescription

        val tmpArrayList = ArrayList<String>()
        if (questionOptions != null) {
            for (item in questionOptions) {
                tmpArrayList.add(item)
            }
        } else {
            Log.e("BankSingleAnswerQuestionActivity", "questionOptions is empty")
        }

        recyclerView = findViewById(R.id.questionOptionsRecyclerView)
        val adapter = QuestionOptionsRecyclerViewAdapter(this, tmpArrayList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        pullExit()
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
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

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        val intent = Intent(this, GroupListActivity::class.java)
        startActivity(intent)
        finish()
    }



    override fun onBackPressed() {
        val intent = Intent(this, GroupListActivity::class.java)
        startActivity(intent)
        finish()
    }
}