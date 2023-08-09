package com.example.quizbanktest.activity.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.databinding.ActivityMpQuizFinishBinding



class MPQuizFinish: AppCompatActivity() {
    private lateinit var finishQuizBinding: ActivityMpQuizFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishQuizBinding = ActivityMpQuizFinishBinding.inflate(layoutInflater)
        setContentView(finishQuizBinding.root)

        finishQuizBinding.gotoHome.setOnClickListener {
            finish()
        }
    }
}