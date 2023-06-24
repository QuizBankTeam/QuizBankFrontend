package com.example.test.Activity

import android.content.Intent
import android.os.Build
import com.example.test.Adapter.QuestionAdapter
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.SingleQuizBinding
import com.example.test.model.Question

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: SingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private  var casualDuringTime =  ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = SingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)

        init()
        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizBinding.QuestionList.adapter = QuestionAdapter(this, questionlist, casualDuringTime)
//        quizBinding.backBtn.setOnClickListener { finish() }
    }


    private fun init()
    {
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val status = intent.getStringExtra("Key_status")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
        val members = intent.getStringArrayListExtra("Key_members")

//      **需要api 33以上**
//      val questions = intent.getParcelableArrayListExtra("Key_questions", Question::class.java)

        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val tmpCasualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")


        if(tmpCasualDuringTime!= null) {
            this.casualDuringTime = tmpCasualDuringTime
        }
        if (questions != null) {
            questionlist = questions
        }

    }

}
