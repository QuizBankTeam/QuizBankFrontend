package com.example.quizbanktest.activity.quiz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.MPQuizFinishRecordAdapter
import com.example.quizbanktest.adapters.quiz.QuizMember
import com.example.quizbanktest.adapters.quiz.QuizMembersRankingAdapter
import com.example.quizbanktest.databinding.ActivityMpQuizFinishBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuizRecord
import java.util.*
import kotlin.collections.ArrayList


class MPQuizFinish: AppCompatActivity() {
    private lateinit var finishQuizBinding: ActivityMpQuizFinishBinding
    private lateinit var rankingAdapter: QuizMembersRankingAdapter
    private lateinit var recordListAdapter: MPQuizFinishRecordAdapter
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var userAnsOptions: ArrayList< ArrayList<String> >
    private lateinit var userAnsDescription: ArrayList<String>
    private lateinit var questionRecordDate: String
    private lateinit var quizId: String
    private var quizType = Constants.quizTypeCasual
    private var quizMembers = ArrayList<QuizMember>()
    private val membersCorrectRate = ArrayList<Int>()
    private lateinit var quizTitle: String
    private lateinit var startDateTime: String
    private lateinit var endDateTime: String
    private var quizDuringTime = 0
    private var membersStr = ArrayList<String>()
    private  var questionRecordList = ArrayList<QuestionRecord>()
    private lateinit var quizRecord: QuizRecord
    private var userRank = 0
    private var quizSize = ""
    private var correctNum: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishQuizBinding = ActivityMpQuizFinishBinding.inflate(layoutInflater)
        setContentView(finishQuizBinding.root)

        init()
        makeRecords()
        Log.d("members size is", quizMembers.size.toString())
        rankingAdapter = QuizMembersRankingAdapter(this, quizMembers, questionlist.size)
        finishQuizBinding.rankList.layoutManager = LinearLayoutManager(this)
        finishQuizBinding.rankList.adapter = rankingAdapter
        finishQuizBinding.rankList.setHasFixedSize(true)

        recordListAdapter = MPQuizFinishRecordAdapter(this, questionlist, membersCorrectRate, quizMembers.size)
        finishQuizBinding.recordList.layoutManager = LinearLayoutManager(this)
        finishQuizBinding.recordList.adapter = recordListAdapter
        finishQuizBinding.recordList.setHasFixedSize(true)

        quizSize = questionlist.size.toString()
        finishQuizBinding.correctNum.text = "你是第${userRank}名! \n你答對了 " + correctNum.toString()+ " / " + quizSize + " 題 "

        finishQuizBinding.gotoHome.setOnClickListener {
            backBtn()
        }
        //傳送questionRecordList quizRecord
        finishQuizBinding.gotoRecord.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("確定保存考試紀錄?")
            builder.setPositiveButton("確定") { dialog, which ->
                saveQuizRecord(quizRecord, questionRecordList)
                val intent = Intent()
                intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
                intent.putExtra("Key_quizRecord", quizRecord)
                setResult(RESULT_OK, intent)
                finish()
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }

        //排名頁面
        finishQuizBinding.rankPage.setOnClickListener {
            finishQuizBinding.rankContainer.visibility = View.VISIBLE
            finishQuizBinding.recordContainer.visibility = View.GONE
            finishQuizBinding.rankPage.setBackgroundColor( 0 )
            finishQuizBinding.recordPage.setBackgroundColor( ContextCompat.getColor(this, R.color.choose_page) )
        }
        //作答記錄頁面
        finishQuizBinding.recordPage.setOnClickListener {
            finishQuizBinding.rankContainer.visibility = View.GONE
            finishQuizBinding.recordContainer.visibility = View.VISIBLE
            finishQuizBinding.rankPage.setBackgroundColor( ContextCompat.getColor(this, R.color.choose_page) )
            finishQuizBinding.recordPage.setBackgroundColor( 0 )
        }
    }
    private fun init(){
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val userAnsOptions  = intent.getSerializableExtra("Key_userAnsOptions") as ArrayList<ArrayList<String>>?
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val startDateTime = intent.getStringExtra("Key_startDateTime")
        val endDateTime = intent.getStringExtra("Key_endDateTime")
        val quizMembers = intent.getSerializableExtra("Key_quizMembers")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)

        if (id != null) {
            this.quizId = id
        }
        if (title != null) {
            this.quizTitle = title
        }
        if (questions != null) {
            questionlist = questions
        }
        if (startDateTime != null) {
            this.startDateTime = startDateTime
        }
        if (endDateTime != null) {
            this.endDateTime  = endDateTime
        }
        if (userAnsOptions != null) {
            this.userAnsOptions = userAnsOptions
        }
        if (startDateTime != null) {
            questionRecordDate = startDateTime.substring(0, 10)
        }else{
            questionRecordDate = ""
        }
        if(quizMembers!=null){
            this.quizMembers = quizMembers as ArrayList<QuizMember>
            this.quizMembers.sortByDescending { it.correctAnswerNum }
            for(index in quizMembers.indices){
                membersStr.add(quizMembers[index].userID)
                if(quizMembers[index].userID==Constants.userId){
                    userRank = index+1
                    showUserFigure(quizMembers[index].correctAnswerNum)
                }
            }
        }

        quizDuringTime = duringTime

    }
    private fun makeRecords(){
        val quizRecordId = UUID.randomUUID().toString()
        val questionRecordId = ArrayList<String>()
        var totalScore : Int = 0

        for(index in questionlist.indices){
            val tmpId = UUID.randomUUID().toString()
            var isCorrect = false
            var memberCorrectNum = 0
            val tmpQImg =  ArrayList<String>()
            val tmpAImg =  ArrayList<String>()

            questionRecordId.add(tmpId)
            for (item in MPStartQuiz.Companion.questionImageArr[index]) {
                tmpQImg.add(Constants.bitmapToString(item)!!)
            }
            for (item in MPStartQuiz.Companion.answerImageArr[index]) {
                tmpAImg.add(Constants.bitmapToString(item)!!)
            }
            questionlist[index].questionImage = tmpQImg
            questionlist[index].answerImage = tmpAImg

            isCorrect = userAnsOptions[index].toSet() == questionlist[index].answerOptions!!.toSet()
            totalScore = if(isCorrect) totalScore+1 else totalScore

            val tmpQuestionRecord = QuestionRecord(tmpId, Constants.userId, userAnsOptions[index], "none", isCorrect, questionRecordDate, questionlist[index], quizRecordId)
            questionRecordList.add(tmpQuestionRecord)

            for(member in quizMembers){
                if(member.records.size>index){
                    val memberIsCorrect = member.records[index].toSet() == questionlist[index].answerOptions!!.toSet()
                    if(memberIsCorrect) {memberCorrectNum+=1}
                    Log.d("$index members records", member.records[index].toString())
                }else{
                    Log.d("$index members records error 404", "")
                }
            }
            Log.d("q$index total correct num is ", memberCorrectNum.toString())
            membersCorrectRate.add(memberCorrectNum)
        }

        correctNum = totalScore
        quizTitle = quizTitle.ifEmpty { "none" }
        Log.d("totalscore is", ((correctNum*100)/questionlist.size).toString())
        val tmpQuizRecord = QuizRecord(quizRecordId, quizTitle, quizId, quizType, ((correctNum*100)/questionlist.size),
            quizDuringTime, startDateTime, endDateTime, membersStr, questionRecordId)
        this.quizRecord = tmpQuizRecord
    }
    private fun showUserFigure(correctNum: Int){
        val incorrectNum = questionlist.size - correctNum
        val correctDiff = correctNum - incorrectNum

        when {
            correctDiff < -2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image0)
            }
            correctDiff == -2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image1)
            }
            correctDiff == -1 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image2)
            }
            correctDiff == 0 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image3)
            }
            correctDiff == 1 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image4)
            }
            correctDiff == 2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image5)
            }
            correctDiff == 3 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image6)
            }
            correctDiff == 4 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image7)            }
            correctDiff > 4 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image8)            }
        }

        finishQuizBinding.userFigure.visibility = View.VISIBLE
        finishQuizBinding.userFigure.translationZ = 20f
        val animation = AnimationUtils.loadAnimation(this, R.anim.show_user_figure)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                finishQuizBinding.userFigure.postDelayed({
                    finishQuizBinding.root.removeView(finishQuizBinding.userFigure)
                    finishQuizBinding.rankContainer.visibility = View.VISIBLE
                }, 2000)
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        finishQuizBinding.userFigure.startAnimation(animation)
    }

    private fun saveQuizRecord(quizRecord: QuizRecord, questionRecordList: ArrayList<QuestionRecord>){
        ConstantsQuizRecord.postQuizRecord(this, quizRecord, questionRecordList, onSuccess = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
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