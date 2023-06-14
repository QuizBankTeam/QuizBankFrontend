package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.SingleQuestionBinding
import com.example.test.databinding.SingleQuizBinding

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: SingleQuizBinding
    private lateinit var optionlist : ArrayList<Question>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizBinding = SingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
        init()
        quizBinding.QuestionList.adapter = OptionAdapter(this, optionlist)
//        quizBinding.backBtn.setOnClickListener { finish() }
    }

    private fun init()
    {

    }

    
}