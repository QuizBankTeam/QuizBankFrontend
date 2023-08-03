package com.example.quizbanktest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.QuestionOptionsRecyclerViewAdapter

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
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.question_to_bank_in, R.anim.question_to_bank_out)
    }
}