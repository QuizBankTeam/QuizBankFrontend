package com.example.test.Activity

import android.content.Intent
import android.os.Build
import com.example.test.Adapter.QuestionAdapter
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.SingleQuizBinding
import com.example.test.model.Question

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: SingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private var casualDuringTime =  ArrayList<Int>()
    private lateinit var fragM: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = SingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
//        fragM = supportFragmentManager
        init()
        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizBinding.QuestionList.adapter = QuestionAdapter(this, questionlist, casualDuringTime)
        quizBinding.saveBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Key_title", quizBinding.quizTitle.text)
            intent.putParcelableArrayListExtra("Key_questions", questionlist)
            setResult(RESULT_OK, intent)
            finish()
        }
        quizBinding.quizSetting.setOnClickListener { quizSetting() }
    }

    //從singleQuestion傳回singlequiz的內容
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in ", "single Quiz")
        Log.d("request code=", requestCode.toString())
        var tmpQuestion = questionlist[requestCode]
        if (data != null) {
            tmpQuestion.options = data.getStringArrayListExtra("Key_options")
            Log.d("options num", tmpQuestion.options?.size.toString())
            for (i in 0 until data.getIntExtra("Key_tagNum", 0)){
                tmpQuestion.tag?.set(i, data.getStringExtra("Key_tag$i")!! )
            }
            tmpQuestion.title = data.getStringExtra("Key_title")
            tmpQuestion.description = data.getStringExtra("Key_description")
            casualDuringTime[requestCode] = data.getIntExtra("Key_timeLimit", 0)
        }
        questionlist[requestCode] = tmpQuestion
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
        quizBinding.quizTitle.text = title
    }
    private fun quizSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuiz, SingleQuizSetting::class.java)
        startActivityForResult(intent, 1000)
    }

}
