package com.example.quizbanktest.activity.quiz
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.QuestionAdapter
import com.example.quizbanktest.databinding.ActivitySingleQuizBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: ActivitySingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var casualDuringTime : ArrayList<Int>
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var quizAdapter: QuestionAdapter
    private  var duringTime: Int = -1
    private var quizIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivitySingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
        init()
        quizBinding.QuestionList.layoutManager = LinearLayoutManager(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizAdapter = QuestionAdapter(this, questionlist, casualDuringTime)
        quizAdapter.setQuizIndex(quizIndex)
        quizAdapter.setQuizType(quizType)
        quizBinding.QuestionList.adapter = quizAdapter
        quizBinding.backBtn.setOnClickListener {
            val intentBack = Intent()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("確定回到主頁?")
            builder.setMessage("是否保存修改紀錄")
            builder.setPositiveButton("確定") { dialog, which ->
                saveQuiz()
                backToQuizList()
            }
            builder.setNegativeButton("取消"){ dialog, which ->
                setResult(RESULT_CANCELED, intentBack)
                finish()
            }
            builder.show()
        }
        quizBinding.saveBtn.setOnClickListener {
            saveQuiz()
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
        val a = "1"
        val b = a.toInt()
        if(requestCode < 1000)  //從singleQuestion傳回single quiz的內容
        {
            resultFromQuestion(requestCode, resultCode, data)
        }
        else if(requestCode == 1000)    //從quiz setting傳回
        {
            if(resultCode == Constants.RESULT_DELETE) {
                deleteQuiz()
                val intentBack = Intent()
                setResult(Constants.RESULT_DELETE, intentBack)
                finish()
            }
            else if(resultCode == RESULT_OK)
            {
                if (data != null) {
                    title = data.getStringExtra("Key_title")
                    quizStartDateTime = data.getStringExtra("Key_startDateTime").toString()
                    quizEndDateTime = data.getStringExtra("Key_endDateTime").toString()
                    if(quizType=="casual"){
                        quizMembers = data.getStringArrayListExtra("Key_members") as ArrayList<String>
                    }else{
                        duringTime = data.getIntExtra("Key_duringTime", 0)
                    }
                    quizBinding.quizTitle.text = title
                }
            }

        }
        else if(requestCode == 2000) //考完試後回傳
        {
            if(resultCode == RESULT_OK) { //保存考試紀錄
                val intent = Intent()
                intent.setClass(this, SPSingleRecord::class.java)
                val questionRecordList = data?.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
                val quizRecord = data?.getParcelableExtra<QuizRecord>("Key_quizRecord")

                intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
                intent.putExtra("Key_quizRecord", quizRecord)
                intent.putExtra("quiz_index", this.quizIndex)
                startActivity(intent)
            }
            else if(resultCode == RESULT_CANCELED){ //不保存考試紀錄
                finish()
            }
        }
    }

    private fun startQuiz(questionList: ArrayList<Question>, status: String,  duringTime: Int, quizStatus: String,
                          quizMembers: ArrayList<String>, quizTitle: String, quizId: String){
        if(status!="draft"){
            val intent = Intent()
            if(quizType=="casual") {
//                intent.setClass(this, MPStartQuiz::class.java)
                Toast.makeText(this, "多人考試還沒做好", Toast.LENGTH_SHORT).show()
            }else{
                intent.setClass(this, SPStartQuiz::class.java)
                intent.putExtra("Key_id", quizId)
                intent.putExtra("Key_quizTitle", quizTitle)
                intent.putExtra("Key_duringTime", duringTime)
                intent.putExtra("Key_type", quizType)
                intent.putExtra("quiz_index", quizIndex)
                intent.putParcelableArrayListExtra("Key_questions", questionList)
            }
            startActivityForResult(intent, 2000)
        }
        else{
            AlertDialog.Builder(this).setTitle("考試尚未設定完成!").setPositiveButton("我懂", null).show()
        }
    }
    private fun saveQuiz(){
        quizStatus = "ready"
        if(questionlist.isEmpty() || duringTime==0){
            quizStatus = "draft"
        }else{
            for(question in questionlist){
                if(question.answerOptions.isNullOrEmpty() || question.options.isNullOrEmpty() || question.description.isNullOrEmpty()){
                    quizStatus = "draft"
                    break
                }
            }
        }

        val putQuiz = Quiz(quizId, quizTitle, quizType, quizStatus, duringTime, casualDuringTime, quizStartDateTime, quizEndDateTime, quizMembers, questionlist)
        ConstantsQuiz.putQuiz(this, putQuiz, onSuccess = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
    private fun backToQuizList(){
        val intentBack = Intent()
        intentBack.putExtra("Key_title", quizTitle)
        intentBack.putExtra("Key_status", quizStatus)
        intentBack.putExtra("Key_startDateTime", quizStartDateTime)
        intentBack.putExtra("Key_endDateTime", quizEndDateTime)
        intentBack.putParcelableArrayListExtra("Key_questions", questionlist)
        if(quizType=="casual"){
            intentBack.putExtra("Key_casualDuringTime", casualDuringTime)
            intentBack.putStringArrayListExtra("Key_members", quizMembers)
        }else{
            intentBack.putExtra("Key_duringTime", duringTime)
        }
        setResult(RESULT_OK, intentBack)
        finish()
    }
    private fun deleteQuiz(){
        ConstantsQuiz.deleteQuiz(this, quizId, onSuccess = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun init()
    {
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val status = intent.getStringExtra("Key_status")
        val startDate = intent.getStringExtra("Key_startDateTime")
        val endDate = intent.getStringExtra("Key_endDateTime")
        val quizIndex = intent.getIntExtra("quiz_index", 0)
//      **需要api 33以上**
//      val questions = intent.getParcelableArrayListExtra("Key_questions", Question::class.java)
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val members = intent.getStringArrayListExtra("Key_members")
        val tmpCasualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)


        if (questions != null)
            questionlist = questions
        if(tmpCasualDuringTime!= null)
            this.casualDuringTime = tmpCasualDuringTime
        if (members != null)
            quizMembers = members
        if (title != null)
            quizTitle = title
        if (id != null)
            quizId = id
        if (type != null)
            quizType = type
        if (status != null)
            quizStatus = status
        if (startDate != null)
            quizStartDateTime = startDate
        if (endDate != null)
            quizEndDateTime = endDate
        this.quizIndex = quizIndex
        this.duringTime = duringTime
        quizBinding.quizTitle.text = title
    }
    private fun quizSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuiz, SingleQuizSetting::class.java)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_status", quizStatus)
        intent.putExtra("Key_startDateTime", quizStartDateTime)
        intent.putExtra("Key_endDateTime", quizEndDateTime)
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
        if(resultCode== RESULT_OK) {
            if (data != null) {
                tmpQuestion.options = data.getStringArrayListExtra("Key_options")
                tmpQuestion.tag?.clear()
                for (i in 0 until data.getIntExtra("Key_tagNum", 0)) {
                    tmpQuestion.tag?.add(data.getStringExtra("Key_tag$i")!!)
                }
                tmpQuestion.title = data.getStringExtra("Key_title")
                tmpQuestion.description = data.getStringExtra("Key_description")
                tmpQuestion.answerOptions = data.getStringArrayListExtra("Key_answerOptions")
                tmpQuestion.answerDescription = data.getStringExtra("Key_answerDescription")
                tmpQuestion.number = data.getStringExtra("Key_number")
                tmpQuestion.questionType = data.getStringExtra("Key_type")

                if (quizType == "casual") {
                    casualDuringTime[requestCode] = data.getIntExtra("Key_timeLimit", 0)
                    Log.d(
                        "in single quiz return time is ",
                        casualDuringTime[requestCode].toString()
                    )
                    quizAdapter.updateTimeLimit(casualDuringTime[requestCode], requestCode)
                }

                questionlist[requestCode] = tmpQuestion
                quizBinding.QuestionList.adapter?.notifyItemChanged(requestCode)
            }
        }else if(resultCode== Constants.RESULT_DELETE){
            questionlist.removeAt(requestCode)
            quizBinding.QuestionList.adapter?.notifyItemChanged(requestCode)
        }
    }

}
