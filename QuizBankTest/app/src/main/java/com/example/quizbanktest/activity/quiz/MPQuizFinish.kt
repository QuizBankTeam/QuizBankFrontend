package com.example.quizbanktest.activity.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
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
    override fun onBackPressed() {
        finish()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun doubleCheckExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        }
    }
}