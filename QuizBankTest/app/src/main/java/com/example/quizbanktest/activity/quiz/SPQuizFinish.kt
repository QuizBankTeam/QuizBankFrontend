package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import java.util.UUID
import com.example.quizbanktest.R
import com.example.quizbanktest.databinding.ActivitySpQuizFinishBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord

class SPQuizFinish : AppCompatActivity(){
    private lateinit var finishQuizBinding: ActivitySpQuizFinishBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var userAnsOptions: ArrayList< ArrayList<String> >
    private lateinit var userAnsDescription: ArrayList<String>
    private lateinit var questionRecordDate: String
    private lateinit var quizId: String
    private lateinit var quizType: String
    private lateinit var quizTitle: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private var members = ArrayList<String>()
    private  var questionRecordList = ArrayList<QuestionRecord>()
    private lateinit var quizRecord: QuizRecord
    private var quizSize = ""
    private var duringTime: Int = 0
    private var correctNum: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishQuizBinding = ActivitySpQuizFinishBinding.inflate(layoutInflater)
        setContentView(finishQuizBinding.root)

        init()
        makeRecords()
        quizSize = questionlist.size.toString()
        finishQuizBinding.correctNum.text = "你答對了 " + correctNum.toString()+ " / " + quizSize + " 題 !"


        finishQuizBinding.gotoHome.setOnClickListener {
            backBtn()
        }

        //傳送questionRecordList quizRecord
        finishQuizBinding.gotoRecord.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("確定保存考試紀錄?")
            builder.setPositiveButton("確定") { dialog, which ->
                val intent = Intent()
                intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
                intent.putExtra("Key_quizRecord", quizRecord)
                setResult(RESULT_OK, intent)
                finish()
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //傳送questionRecordList quizRecord
    }

    private fun init(){
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val userAnsOptions  = intent.getSerializableExtra("Key_userAnsOptions") as ArrayList<ArrayList<String>>?
        val userAnsDescription = intent.getStringArrayListExtra("Key_userAnsDescription")
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
        val type  = intent.getStringExtra("Key_type")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)
        if (id != null) {
            this.quizId = id
        }
        if (title != null) {
            this.quizTitle = title
        }
        if (type != null) {
            quizType = type
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
        if (userAnsOptions != null) {
            this.userAnsOptions = userAnsOptions
        }
        if (userAnsDescription != null) {
            this.userAnsDescription = userAnsDescription
        }

        if (startDate != null) {
            questionRecordDate = startDate.substring(0, 10)
        }else{
            questionRecordDate = ""
        }
        this.duringTime = duringTime
        members.add("jacky")
    }

    private fun makeRecords(){
        val quizRecordId = UUID.randomUUID().toString()
        val questionRecordId = ArrayList<String>()
        var totalScore : Int = 0
        var hasShorAns = false
        for(index in userAnsOptions.indices){
            for(item1 in userAnsOptions[index]){
                Log.d("確認答案", index.toString()+item1)
            }
        }
        Log.d("answer record size is", userAnsOptions.size.toString())
        for(index in questionlist.indices){
            val tmpId = UUID.randomUUID().toString()
            var isCorrect = false
            Log.d("index is", index.toString())
            questionRecordId.add(tmpId)

            if(questionlist[index].questionType=="ShortAnswer") {
                hasShorAns = true
                setAnswerQuestion(questionlist[index], index, userAnsDescription[index]) //確定答案是否正確
            }
            else {
                isCorrect = userAnsOptions[index].toSet() == questionlist[index].answerOptions!!.toSet()
            }

            totalScore = if(isCorrect) totalScore+1 else totalScore

//            Log.d(index.toString(), "is correct is"+isCorrect.toString())
            val tmpQuestionRecord = QuestionRecord(tmpId, "jacky", userAnsOptions[index], userAnsDescription[index], isCorrect, questionRecordDate, questionlist[index], quizRecordId)
            questionRecordList.add(tmpQuestionRecord)
        }
        if(!hasShorAns){
            finishQuizBinding.decideShortAns.text = ""
        }
        correctNum = totalScore
        val tmpQuizRecord = QuizRecord(quizRecordId, quizTitle, quizId, quizType, totalScore/questionlist.size,
                                        duringTime, startDate, endDate, members, questionRecordId)
        this.quizRecord = tmpQuizRecord
    }

    private fun setAnswerQuestion(question: Question, index: Int, userAns: String){
        val v: View = layoutInflater.inflate(R.layout.item_decide_iscorrect, finishQuizBinding.questionContainer, false)
        val answerDesc: TextView = v.findViewById(R.id.answer_description)
        val questionDesc: TextView = v.findViewById(R.id.question_description)
        val answerSwitch: SwitchCompat = v.findViewById(R.id.answer_switch)
        val isCorrectTag: TextView = v.findViewById(R.id.answer_isCorrect)
        val questionNum:  TextView = v.findViewById(R.id.question_number)

        answerDesc.text = userAns
        questionDesc.text = question.description
        questionNum.text = "第" + question.number + "題"
        answerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                isCorrectTag.text = "正確"
                isCorrectTag.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                questionRecordList[index].correct = true
                correctNum++
                finishQuizBinding.correctNum.text = "你答對了 " +correctNum.toString()+ " / " +quizSize+ " 題 !"
            }else{
                isCorrectTag.text = "錯誤"
                isCorrectTag.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_wrong))
                questionRecordList[index].correct = false
                correctNum--
                finishQuizBinding.correctNum.text = "你答對了 " +correctNum.toString()+ " / " +quizSize+ " 題 !"
            }
        }
        finishQuizBinding.questionContainer.addView(v)
    }
    private fun backBtn(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("確定回到主頁?")
        builder.setMessage("將不會保存考試紀錄")
        builder.setPositiveButton("確定") { dialog, which ->
            setResult(RESULT_CANCELED)
            finish()
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }

    override fun onBackPressed() {
        backBtn()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun doubleCheckExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                backBtn()
            }
        }
    }
}