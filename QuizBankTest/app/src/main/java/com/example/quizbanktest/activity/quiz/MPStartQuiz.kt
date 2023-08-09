package com.example.quizbanktest.activity.quiz
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.databinding.ActivityMpStartQuizBinding
import com.example.quizbanktest.models.Question


class  MPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: ActivityMpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var questionlist : ArrayList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = ActivityMpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()


    }
    private fun init(){

    }
}