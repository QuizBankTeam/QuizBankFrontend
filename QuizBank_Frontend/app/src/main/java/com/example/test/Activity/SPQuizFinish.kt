package com.example.test.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.test.model.Question

class SPQuizFinish : AppCompatActivity(){
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var answerRecords: ArrayList< ArrayList<String> >
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        makeRecords()
    }
    private fun init(){
        val intent = Intent()
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val answerRecords  = getIntent().getSerializableExtra("Key_answerRecords") as ArrayList<ArrayList<String>>?
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")

        if(answerRecords==null){
            Log.d("answerRecords is null", "")
        }else{
            Log.d("answerRecords is not null", "")
        }
        if (id != null) {
            this.quizId = id
        }
        if (title != null) {
            this.quizTitle = title
        }
        if (questions != null) {
            questionlist = questions
        }
        if (startDate != null) {
            this.startDate = startDate
        }
        if (endDate != null) {
            this.endDate  = endDate
        }
        if (answerRecords != null) {
            this.answerRecords = answerRecords
        }
    }
    private fun makeRecords(){
        for(index in questionlist.indices){

        }
    }
}