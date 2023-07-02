package com.example.test.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.test.databinding.MpStartQuizBinding
import com.example.test.model.Question

class MPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: MpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var questionlist : ArrayList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = MpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()


    }
    private fun init(){

    }
}