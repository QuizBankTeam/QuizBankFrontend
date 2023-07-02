package com.example.test.Activity

import android.app.AlertDialog
import android.content.Intent
import com.example.test.Adapter.QuestionAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.SingleQuizBinding
import com.example.test.model.Question

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: SingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var casualDuringTime : ArrayList<Int>
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDate: String
    private lateinit var quizEndDate: String
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var quizAdapter: QuestionAdapter
    private  var duringTime: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = SingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
        init()

        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizAdapter = QuestionAdapter(this, questionlist, casualDuringTime)
        quizAdapter.setQuizType(quizType)
        quizBinding.QuestionList.adapter = quizAdapter
        quizBinding.saveBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Key_title", quizTitle)
            intent.putExtra("Key_startDate", quizStartDate)
            intent.putExtra("Key_endDate", quizEndDate)
            intent.putParcelableArrayListExtra("Key_questions", questionlist)
            if(quizType=="casual"){
                intent.putExtra("Key_casualDuringTime", casualDuringTime)
                intent.putStringArrayListExtra("Key_members", quizMembers)
            }else{
                intent.putExtra("Key_duringTime", duringTime)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        quizBinding.quizSetting.setOnClickListener { quizSetting() }
        quizBinding.startQuiz.setOnClickListener{
            startQuiz(questionlist, quizStatus, duringTime, quizStatus,  quizMembers, quizTitle, quizId)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in ", "single Quiz")
        Log.d("request code=", requestCode.toString())

        if(requestCode!=1000) {
            //從singleQuestion傳回singlequiz的內容
            resultFromQuestion(requestCode, resultCode, data)
        }
        else {
            //從quiz setting傳回
            if(resultCode == RESULT_CANCELED)
                Toast.makeText(this, "modify nothing", Toast.LENGTH_SHORT).show()
            else if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "modify something", Toast.LENGTH_SHORT).show()
                if (data != null) {
                    title = data.getStringExtra("Key_title")
                    quizStartDate = data.getStringExtra("Key_startDate").toString()
                    quizEndDate = data.getStringExtra("Key_endDate").toString()
                    if(quizType=="casual"){
                        quizMembers = data.getStringArrayListExtra("Key_members") as ArrayList<String>
                    }else{
                        duringTime = data.getIntExtra("Key_duringTime", 0)
                    }
                    quizBinding.quizTitle.text = title
                }
            }
        }
    }

    private fun startQuiz(questionList: ArrayList<Question>, status: String,  duringTime: Int, quizStatus: String,
                          quizMembers: ArrayList<String>, quizTitle: String, quizId: String){
        if(status=="ready"){
            val intent = Intent()
            if(quizType=="casual") {
//                intent.setClass(this, MPStartQuiz::class.java)
                Toast.makeText(this, "多人考試還沒做好", Toast.LENGTH_SHORT).show()
            }else{
                intent.setClass(this, SPStartQuiz::class.java)
                intent.putExtra("Key_id", quizId)
                intent.putExtra("Key_quizTitle", quizTitle)
                intent.putExtra("Key_duringTime", duringTime)
                intent.putParcelableArrayListExtra("Key_questions", questionList)
            }
            startActivity(intent)
        }
        else{
            AlertDialog.Builder(this).setTitle("至少要有一個正確選項!").setPositiveButton("我懂", null).show()
        }
    }
    private fun init()
    {
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val status = intent.getStringExtra("Key_status")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
//      **需要api 33以上**
//      val questions = intent.getParcelableArrayListExtra("Key_questions", Question::class.java)
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")

        if(type=="casual"){
            val members = intent.getStringArrayListExtra("Key_members")
            val tmpCasualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")
            if(tmpCasualDuringTime!= null)
                this.casualDuringTime = tmpCasualDuringTime
            if (members != null)
                quizMembers = members
        }
        else{
            val duringTime = intent.getIntExtra("Key_duringTime", 0)
            this.duringTime = duringTime
            this.casualDuringTime = ArrayList()
            quizMembers = ArrayList()
        }


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

        quizBinding.quizTitle.text = title
    }
    private fun quizSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuiz, SingleQuizSetting::class.java)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_status", quizStatus)
        intent.putExtra("Key_startDate", quizStartDate)
        intent.putExtra("Key_endDate", quizEndDate)
        intent.putExtra("Key_type", quizType)
        if(quizTitle=="casual") {
            intent.putStringArrayListExtra("Key_members", quizMembers)
        }
        else{
            intent.putExtra("Key_duringTime", duringTime)
        }
        startActivityForResult(intent, 1000)
    }

    private fun resultFromQuestion(requestCode: Int, resultCode: Int, data: Intent?){
        val tmpQuestion = questionlist[requestCode]
        if (data != null) {
            tmpQuestion.options = data.getStringArrayListExtra("Key_options")
            tmpQuestion.tag?.clear()
            for (i in 0 until data.getIntExtra("Key_tagNum", 0)) {
                tmpQuestion.tag?.add(data.getStringExtra("Key_tag$i")!!)
            }
            tmpQuestion.title = data.getStringExtra("Key_title")
            tmpQuestion.description = data.getStringExtra("Key_description")
            tmpQuestion.answerOption = data.getStringArrayListExtra("Key_answerOptions")
            tmpQuestion.answerDescription = data.getStringExtra("Key_answerDescription")
            tmpQuestion.number = data.getStringExtra("Key_number")
            tmpQuestion.type = data.getStringExtra("Key_type")

            if(quizType=="casual") {
                casualDuringTime[requestCode] = data.getIntExtra("Key_timeLimit", 0)
                Log.d("in single quiz return time is ", casualDuringTime[requestCode].toString())
                quizAdapter.updateTimeLimit(casualDuringTime[requestCode], requestCode)
            }

            questionlist[requestCode] = tmpQuestion
            quizBinding.QuestionList.adapter?.notifyItemChanged(requestCode)
        }
    }

}
