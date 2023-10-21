package com.example.quizbanktest.activity.quiz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.*
import com.example.quizbanktest.databinding.ActivityMpQuizFinishBinding
import com.example.quizbanktest.fragment.QuestionAddDialog
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.network.quizService
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import com.example.quizbanktest.utils.ConstantsQuizRecord
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private lateinit var sendQList: RecyclerView
    private lateinit var sendingQuestion: Question
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

        //傳送題目
        recordListAdapter.setOnAddListener(object : MPQuizFinishRecordAdapter.SelectOnAddListener{
            override fun onAdd(position: Int) {
                val qImage = MPStartQuiz.Companion.questionImageArr[position]
                val aImage = MPStartQuiz.Companion.answerImageArr[position]
                val qImageB64 = ArrayList<String>()
                val aImageB64 = ArrayList<String>()
                qImage.forEach {
                    qImageB64.add(Constants.bitmapToString(it)!!)
                }
                aImage.forEach {
                    aImageB64.add(Constants.bitmapToString(it)!!)
                }
                sendingQuestion = questionlist[position].copy(_id = UUID.randomUUID().toString())
                sendingQuestion.questionImage = qImageB64
                sendingQuestion.answerImage = aImageB64
                showAddDialog()
            }
        })

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

            val tmpQuestionRecord = QuestionRecord(tmpId, userAnsOptions[index], "none", isCorrect, questionlist[index])
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
        val tmpQuizRecord = QuizRecord(quizRecordId, quizTitle, quizId, Constants.userId, quizType, ((correctNum*100)/questionlist.size),
            quizDuringTime, startDateTime, endDateTime, membersStr, questionRecordId)
        this.quizRecord = tmpQuizRecord
    }
    private fun showUserFigure(correctNum: Int){
        val incorrectNum = questionlist.size - correctNum
        val correctDiff = correctNum - incorrectNum

        when {
            correctDiff < -2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image0)
                val player = MediaPlayer.create(this, R.raw.figure_music0)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == -2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image1)
                val player = MediaPlayer.create(this, R.raw.figure_music1)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == -1 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image2)
                val player = MediaPlayer.create(this, R.raw.figure_music2)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == 0 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image3)
                val player = MediaPlayer.create(this, R.raw.figure_music3)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == 1 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image4)
                val player = MediaPlayer.create(this, R.raw.figure_music4)
                player.setVolume(50.0f, 50.0f)
                player.start()
            }
            correctDiff == 2 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image5)
                val player = MediaPlayer.create(this, R.raw.figure_music5)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == 3 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image6)
                val player = MediaPlayer.create(this, R.raw.figure_music6)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
            correctDiff == 4 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image7)
                val player = MediaPlayer.create(this, R.raw.figure_music6)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }

            correctDiff > 4 -> {
                finishQuizBinding.userFigure.setImageResource(R.drawable.figure_image8)
                val player = MediaPlayer.create(this, R.raw.figure_music7)
                player.setVolume(30.0f, 30.0f)
                player.start()
            }
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
                    finishQuizBinding.finishTitle.visibility = View.VISIBLE
                    finishQuizBinding.pageContainer.visibility = View.VISIBLE
                    finishQuizBinding.rankContainer.visibility = View.VISIBLE

                }, 2000)
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        finishQuizBinding.userFigure.startAnimation(animation)
    }
    private fun showAddDialog(){
        val sendDialog = BottomSheetDialog(this)
        sendDialog.setContentView(R.layout.dialog_mp_send_question)
        val sp: ImageView? = sendDialog.findViewById(R.id.send_to_singleQuiz)
        val mp: ImageView? = sendDialog.findViewById(R.id.send_to_multiQuiz)
        val backBtn: Button? = sendDialog.findViewById(R.id.cancel_btn)
        val baseView: LinearLayout? = sendDialog.findViewById(R.id.base_view)
        val qList: RecyclerView? = sendDialog.findViewById(R.id.quiz_list)
        val cancelBtnStr = "❌"
        val backBtnStr = "⬅"
        if (qList != null) {
            sendQList = qList
        }

        sp?.setOnClickListener {
            backBtn?.text = backBtnStr
            baseView?.visibility = View.GONE
            qList?.visibility = View.VISIBLE
            showQuiz(Constants.quizTypeSingle, sendDialog)
        }
        mp?.setOnClickListener {
            backBtn?.text = backBtnStr
            baseView?.visibility = View.GONE
            qList?.visibility = View.VISIBLE
            showQuiz(Constants.quizTypeCasual, sendDialog)
        }
        backBtn?.setOnClickListener {
            if(backBtn.text ==cancelBtnStr){
                sendDialog.dismiss()
            }else{
                backBtn.text = cancelBtnStr
                baseView?.visibility = View.VISIBLE
                qList?.visibility = View.GONE
            }
        }
        sendDialog.show()
    }
    private fun showQuiz(quizType: String, dialog: BottomSheetDialog){
        ConstantsQuiz.getAllQuizsWithBatch(this, quizType, batch = 0, onSuccess = { quizList ->
            if (quizList != null) {
                if(quizList.size > 0) {
                    val adapter = MPQuizFinishSendQAdapter(this, quizList)
                    sendQList.layoutManager = LinearLayoutManager(this)
                    sendQList.setHasFixedSize(true)
                    sendQList.adapter = adapter

                    adapter.setOnSendListener(object : MPQuizFinishSendQAdapter.OnSendListener{
                        override fun onSend(position: Int) {
                            val putQuiz = quizList[position]
                            putQuiz.questions?.add(sendingQuestion)
                            if(quizType==Constants.quizTypeCasual){
                                putQuiz.duringTime = putQuiz.duringTime?.plus(20)
                                putQuiz.casualDuringTime?.add(20)
                            }
                            dialog.dismiss()
                            ConstantsQuiz.putQuiz(this@MPQuizFinish, putQuiz, onSuccess = {
                                Toast.makeText(this@MPQuizFinish, "儲存成功", Toast.LENGTH_SHORT).show()
                            }, onFailure = {
                                Toast.makeText(this@MPQuizFinish, it, Toast.LENGTH_SHORT).show()
                            })
                        }
                    })
                }else{
                    Log.d("No quiz List", "")
                    val tmpPostQuiz = quizService.PostQuiz("預設題庫", quizType, Constants.quizStatusDraft, 60, ArrayList(),
                        "", "", ArrayList(), ArrayList()
                    )
                    ConstantsQuiz.postQuiz(this, tmpPostQuiz, onSuccess = { postQuiz ->
                        val newQuiz = ArrayList<Quiz>()
                        newQuiz.add(postQuiz)
                        val adapter = MPQuizFinishSendQAdapter(this, newQuiz)
                        sendQList.layoutManager = LinearLayoutManager(this)
                        sendQList.setHasFixedSize(true)
                        sendQList.adapter = adapter

                        adapter.setOnSendListener(object : MPQuizFinishSendQAdapter.OnSendListener{
                            override fun onSend(position: Int) {
                                postQuiz.questions?.add(sendingQuestion)
                                if(quizType==Constants.quizTypeCasual){
                                    postQuiz.duringTime = postQuiz.duringTime?.plus(20)
                                    postQuiz.casualDuringTime?.add(20)
                                }
                                Log.d("no quiz list sending q", "")
                                dialog.dismiss()
                                ConstantsQuiz.putQuiz(this@MPQuizFinish, postQuiz, onSuccess = {
                                    Toast.makeText(this@MPQuizFinish, "儲存成功", Toast.LENGTH_SHORT).show()
                                }, onFailure = {
                                    Toast.makeText(this@MPQuizFinish, it, Toast.LENGTH_SHORT).show()
                                })
                            }
                        })
                    }, onFailure = {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
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
        backBtn()
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