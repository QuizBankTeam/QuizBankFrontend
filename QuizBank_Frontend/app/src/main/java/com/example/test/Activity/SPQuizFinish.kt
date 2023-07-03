package com.example.test.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Adapter.OptionAdapter
import com.example.test.databinding.SpStartQuizBinding
import com.example.test.model.Option
import com.example.test.model.Question

class SPQuizFinish : AppCompatActivity(){
    private lateinit var finishQuizBinding: SpStartQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var answerRecords: ArrayList< ArrayList<Int> >
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var optionAdapter: OptionAdapter
    private var currentAtQuestion: Int = 0
    private var currentSelection = ArrayList<Int>() //被選過的option

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishQuizBinding = SpStartQuizBinding.inflate(layoutInflater)
        setContentView(finishQuizBinding.root)

        init()
//        makeRecords()
//        setQuestion()
        finishQuizBinding.btnSubmit.setOnClickListener {
            questionSubmit()
        }
    }
    private fun init(){
        val intent = Intent()
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val answerRecords  = getIntent().getSerializableExtra("Key_answerRecords") as ArrayList<ArrayList<Int>>?
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")

        if(answerRecords==null){
            Log.d("answerRecords is null", "")
        }else{
            Log.d("answerRecords is not null", "")
            Log.d("answerRecords is" , answerRecords.toString())
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
        else
        {
            Log.d("question is null","")
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
//    private fun setQuestion(){
//        val optionNum = arrayOf("A", "B", "C", "D", "E", "F")
//        val currentQuestion = questionlist[currentAtQuestion]
//        val optionlist : ArrayList<Option> = ArrayList()
//        if(currentQuestion.type=="MultipleChoiceS"){
//            finishQuizBinding.QuestionType.text = "單選題"
//        }
//        else if(currentQuestion.type=="MultipleChoiceM"){
//            finishQuizBinding.QuestionType.text = "多選題"
//        }
//        for(index in currentQuestion.options?.indices!!){
//            val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
//            optionlist.add(tmpOption)
//        }
//        optionAdapter = OptionAdapter(this, optionlist)
//        finishQuizBinding.QuestionOption.adapter = optionAdapter
//        finishQuizBinding.questionDescription.text = currentQuestion.description
//        finishQuizBinding.QuestionImage.setImageResource(currentQuestion.image)
//        currentSelection.clear()
//        finishQuizBinding.progressBar.progress = currentAtQuestion+1
//        finishQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionlist.size.toString()
//        currentSelection = answerRecords[currentAtQuestion]
//        optionAdapter.setSelectOptions(currentSelection)
//    }
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun questionSubmit(){
        val currentQuestion = questionlist[currentAtQuestion]
        val options = currentQuestion.options!!
            if( currentAtQuestion < questionlist.size )
            {
                if(currentAtQuestion == questionlist.size-1)
                {
                    finish()
                }
                else
                {
                    currentAtQuestion += 1
//                    setQuestion()
                }
            }
    }
//    private fun makeRecords(){
//        for(index in answerRecords.indices){
//
//        }
//    }
}