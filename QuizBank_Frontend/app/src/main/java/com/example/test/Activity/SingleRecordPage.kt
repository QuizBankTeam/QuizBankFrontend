package com.example.test.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Adapter.OptionAdapter
import com.example.test.databinding.SpSingleRecordBinding
import com.example.test.model.Option
import com.example.test.model.Question
import com.example.test.model.QuestionRecord
import com.example.test.model.QuizRecord

class SingleRecordPage: AppCompatActivity()  {
    private lateinit var singleRecordBinding: SpSingleRecordBinding
    private lateinit var questionList: ArrayList<Question>
    private lateinit var recordList: ArrayList<QuestionRecord>
    private lateinit var quizRecord: QuizRecord
    private lateinit var optionAdapter: OptionAdapter
    private var currentAtQuestion: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singleRecordBinding = SpSingleRecordBinding.inflate(layoutInflater)
        setContentView(singleRecordBinding.root)

        init()
        setQuestion()
    }

    private fun init(){
        val spRecordList = intent.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
        val questionList = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val spQuizRecord = intent.getParcelableExtra<QuizRecord>("Key_quizRecord")

        if (questionList != null) {
            this.questionList = questionList
        }
        if (spRecordList != null) {
            this.recordList = spRecordList
        }
        if (spQuizRecord != null) {
            this.quizRecord = spQuizRecord
        }
    }

    private fun setQuestion(){
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F")
        val currentQuestion = questionList[currentAtQuestion]
        val optionlist : ArrayList<Option> = ArrayList()
        if(currentQuestion.type=="MultipleChoiceS"){
            singleRecordBinding.QuestionType.text = "單選題"
        }
        else if(currentQuestion.type=="MultipleChoiceM"){
            singleRecordBinding.QuestionType.text = "多選題"
        }
        else if(currentQuestion.type=="ShortAnswer"){
            singleRecordBinding.QuestionType.text = "簡答題"
        }
        else if(currentQuestion.type=="TrueOrFalse"){
            singleRecordBinding.QuestionType.text = "是非題"
        }
        else{
            singleRecordBinding.QuestionType.text = "填空題"
        }

        for(index in currentQuestion.options?.indices!!){
            val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
            optionlist.add(tmpOption)
        }

        optionAdapter = OptionAdapter(this, optionlist)
        optionAdapter.setInStartQuiz(true)
        singleRecordBinding.QuestionOption.adapter = optionAdapter
        singleRecordBinding.questionDescription.text = currentQuestion.description
        singleRecordBinding.QuestionImage.setImageResource(currentQuestion.image)

        singleRecordBinding.progressBar.progress = currentAtQuestion+1
        singleRecordBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionList.size.toString()
    }
}