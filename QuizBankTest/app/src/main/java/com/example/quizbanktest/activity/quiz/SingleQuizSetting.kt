package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import com.example.quizbanktest.databinding.ActivityMpSingleQuizSettingBinding
import com.example.quizbanktest.databinding.ActivitySpSingleQuizSettingBinding
import com.example.quizbanktest.R
import com.example.quizbanktest.utils.Constants

class SingleQuizSetting: AppCompatActivity() {
    private lateinit var mpQuizSetAttrBinding: ActivityMpSingleQuizSettingBinding
    private lateinit var spQuizSetAttrBinding: ActivitySpSingleQuizSettingBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private var quizMembers = ArrayList<String>()
    private var quizDuringTime : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        if(quizType=="casual"){
            mpQuizSetAttrBinding.backBtn.setOnClickListener {
                backBtn()
            }
            mpQuizSetAttrBinding.saveBtn.setOnClickListener {
                saveBtn()
            }
            mpQuizSetAttrBinding.QuizDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("確定刪除考試?")
                builder.setPositiveButton("確定") { dialog, which ->
                    val intentBack = Intent()
                    setResult(Constants.RESULT_DELETE, intentBack)
                    finish()
                }
                builder.setNegativeButton("取消", null)
                builder.show()
            }
        }
        else if(quizType=="single"){
            spQuizSetAttrBinding.backBtn.setOnClickListener {
                backBtn()
            }
            spQuizSetAttrBinding.saveBtn.setOnClickListener {
                saveBtn()
            }
            spQuizSetAttrBinding.QuizDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("確定刪除考試?")
                builder.setPositiveButton("確定") { dialog, which ->
                    val intentBack = Intent()
                    setResult(Constants.RESULT_DELETE, intentBack)
                    finish()
                }
                builder.setNegativeButton("取消", null)
                builder.show()
            }
        }
    }
    private fun init(){
        val title = intent.getStringExtra("Key_title")
        val status = intent.getStringExtra("Key_status")
        val startDateTime = intent.getStringExtra("Key_startDateTime")
        val endDateTime = intent.getStringExtra("Key_endDateTime")
        val type = intent.getStringExtra("Key_type")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)
        val members = intent.getStringArrayListExtra("Key_members")
        var MembersStr = "無成員"

        if (title != null)
            this.quizTitle = title
        if (status != null)
            this.quizStatus = status
        if (startDateTime != null)
            this.quizStartDateTime = startDateTime
        if (endDateTime != null)
            this.quizEndDateTime = endDateTime
        if (type != null)
            quizType = type
        if (members != null) {
            this.quizMembers = members
            if(members.size>0) {
                MembersStr = ""
                for(item in members){
                    MembersStr = "$MembersStr$item "
                }
            }
        }
        quizDuringTime = duringTime

        if(type=="casual") {
            mpQuizSetAttrBinding = ActivityMpSingleQuizSettingBinding.inflate(layoutInflater)
            setContentView(mpQuizSetAttrBinding.root)
            mpQuizSetAttrBinding.QuizMembers.text = MembersStr
            mpQuizSetAttrBinding.QuizTitle.setText(title)
            mpQuizSetAttrBinding.QuizStatus.text = status
            mpQuizSetAttrBinding.QuizStartDate.text = startDateTime
            mpQuizSetAttrBinding.QuizEndDate.text = endDateTime
            mpQuizSetAttrBinding.QuizDuringTimeMin.text = (duringTime/60).toString()
            mpQuizSetAttrBinding.QuizDuringTimeSec.text = (duringTime%60).toString()
        }
        else if(type=="single")
        {
            spQuizSetAttrBinding = ActivitySpSingleQuizSettingBinding.inflate(layoutInflater)
            setContentView(spQuizSetAttrBinding.root)
            spQuizSetAttrBinding.QuizMembers.text = MembersStr
            spQuizSetAttrBinding.QuizTitle.setText(title)
            spQuizSetAttrBinding.QuizStatus.text = status
            spQuizSetAttrBinding.QuizStartDate.text = startDateTime
            spQuizSetAttrBinding.QuizEndDate.text = endDateTime
            spQuizSetAttrBinding.QuizDuringTimeMin.setText((duringTime/60).toString())
            spQuizSetAttrBinding.QuizDuringTimeSec.setText((duringTime%60).toString())
        }
    }
    private fun saveBtn(){
        val intentBack = Intent()
        if(quizType==Constants.quizTypeSingle){
            val titleText = spQuizSetAttrBinding.QuizTitle.text.toString()
            val duringTimeMinStr : String = spQuizSetAttrBinding.QuizDuringTimeMin.text.toString()
            val duringTimeSecStr : String = spQuizSetAttrBinding.QuizDuringTimeSec.text.toString()
            val duringTimeMin = if(duringTimeMinStr.isEmpty()) 0 else duringTimeMinStr.toInt()
            val duringTimeSec = if(duringTimeSecStr.isEmpty()) 0 else duringTimeSecStr.toInt()
            val duringTime = duringTimeMin*60 + duringTimeSec
            if(duringTimeMin>200 || duringTime == 0 || duringTimeSec>59){
                AlertDialog.Builder(this).setTitle("考試時長設定有誤!").setPositiveButton("我懂", null).show()
            }
            intentBack.putExtra("Key_title", titleText)
            intentBack.putExtra("Key_startDateTime", quizStartDateTime)
            intentBack.putExtra("Key_endDateTime", quizEndDateTime)
            intentBack.putExtra("Key_duringTime", duringTimeMin*60 + duringTimeSec )
            setResult(RESULT_OK, intentBack)
            finish()
        }else if(quizType==Constants.quizTypeCasual){
            val titleText = mpQuizSetAttrBinding.QuizTitle.text.toString()
//            Log.d("title text is ", titleText)
            intentBack.putExtra("Key_title", titleText)
            intentBack.putExtra("Key_startDateTime", quizStartDateTime)
            intentBack.putExtra("Key_endDateTime", quizEndDateTime)
            intentBack.putStringArrayListExtra("Key_members", quizMembers)
        }
        setResult(RESULT_OK, intentBack)
        finish()
    }
    private fun backBtn(){
        setResult(RESULT_CANCELED)
        finish()
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

