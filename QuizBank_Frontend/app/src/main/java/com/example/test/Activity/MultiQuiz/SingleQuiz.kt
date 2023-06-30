package com.example.test.Activity.MultiQuiz

import android.content.Intent
import com.example.test.Adapter.MultiQuiz.QuestionAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.MpSingleQuizBinding
import com.example.test.model.Question

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: MpSingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var casualDuringTime : ArrayList<Int>
    private lateinit var fragM: FragmentManager
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDate: String
    private lateinit var quizEndDate: String
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var quizAdapter: QuestionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = MpSingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
//        fragM = supportFragmentManager
        init()
        println("it's now on creating")
        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizAdapter = QuestionAdapter(this, questionlist, casualDuringTime)
        quizBinding.QuestionList.adapter = quizAdapter
        quizBinding.saveBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Key_title", quizTitle)
            intent.putExtra("Key_startDate", quizStartDate)
            intent.putExtra("Key_endDate", quizEndDate)
            intent.putExtra("Key_casualDuringTime", casualDuringTime)
            intent.putStringArrayListExtra("Key_members", quizMembers)
            intent.putParcelableArrayListExtra("Key_questions", questionlist)
            setResult(RESULT_OK, intent)
            finish()
        }
        quizBinding.quizSetting.setOnClickListener { quizSetting() }
        quizBinding.startQuiz.setOnClickListener{
            startQuiz(questionlist, quizStatus)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in ", "single Quiz")
        Log.d("request code=", requestCode.toString())

        if(requestCode!=1000)
        {
            //從singleQuestion傳回singlequiz的內容
            var tmpQuestion = questionlist[requestCode]
            if (data != null) {
                tmpQuestion.options = data.getStringArrayListExtra("Key_options")
                tmpQuestion.tag?.clear()
                for (i in 0 until data.getIntExtra("Key_tagNum", 0)){
                    tmpQuestion.tag?.add(data.getStringExtra("Key_tag$i")!!)
                }
                tmpQuestion.title = data.getStringExtra("Key_title")
                tmpQuestion.description = data.getStringExtra("Key_description")
                casualDuringTime[requestCode] = data.getIntExtra("Key_timeLimit", 0)
                tmpQuestion.answerOption = data.getStringArrayListExtra("Key_answerOptions")
                tmpQuestion.answerDescription = data.getStringExtra("Key_answerDescription")
                tmpQuestion.number = data.getStringExtra("Key_number")
                tmpQuestion.type = data.getStringExtra("Key_type")
                quizAdapter.updateTimeLimit(casualDuringTime[requestCode], requestCode)
                questionlist[requestCode] = tmpQuestion
                quizBinding.QuestionList.adapter?.notifyItemChanged(requestCode)
            }

        }
        else
        {
            //從singlequizsetting傳回
            if(resultCode == RESULT_CANCELED)
                Toast.makeText(this, "modify nothing", Toast.LENGTH_SHORT).show()
            else if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "modify something", Toast.LENGTH_SHORT).show()
                if (data != null) {
                    title = data.getStringExtra("Key_title")
                    quizStartDate = data.getStringExtra("Key_startDate").toString()
                    quizEndDate = data.getStringExtra("Key_endDate").toString()
                    quizMembers = data.getStringArrayListExtra("Key_members") as ArrayList<String>
                    quizBinding.quizTitle.text = title
                }
            }
        }
    }

    private fun startQuiz(questionList: ArrayList<Question>, status: String){
        
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
        quizBinding.quizTitle.text = title

        if(tmpCasualDuringTime!= null)
            this.casualDuringTime = tmpCasualDuringTime
        if (questions != null)
            questionlist = questions
        if (title != null)
            quizTitle = title
        if (id != null)
            quizId = id
        if (type != null)
            quizType = type
        if (status != null)
            quizStatus = status
        if (startDate != null)
            quizStartDate = startDate
        if (endDate != null)
            quizEndDate = endDate
        if (members != null)
            quizMembers = members

    }
    private fun quizSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuiz, SingleQuizSetting::class.java)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_status", quizStatus)
        intent.putExtra("Key_startDate", quizStartDate)
        intent.putExtra("Key_endDate", quizEndDate)
        intent.putStringArrayListExtra("Key_members", quizMembers)
        startActivityForResult(intent, 1000)
    }

}
