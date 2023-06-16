package com.example.test.Activity

import android.content.Intent
import com.example.test.Adapter.QuestionAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.SingleQuizBinding
import com.example.test.model.Question

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: SingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = SingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)

        init()
        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
//        quizBinding.QuestionList.adapter = QuestionAdapter(this, questionlist)
//        quizBinding.backBtn.setOnClickListener { finish() }
    }

    private fun init()
    {
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val status = intent.getStringExtra("Key_status")
        val duringTime = intent.getStringExtra("Key_duringTime")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
        val members = intent.getStringExtra("Key_members")
//        val questions = intent.getStringArrayListExtra("Key_questions")
//        questionlist = questions
    }

    
}