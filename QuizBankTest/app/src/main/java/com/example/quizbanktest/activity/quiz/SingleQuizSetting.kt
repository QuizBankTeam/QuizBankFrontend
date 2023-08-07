package com.example.quizbanktest.activity.quiz
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var quizMembers: ArrayList<String>
    private var quizDuringTime : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        if(quizType=="casual"){
            mpQuizSetAttrBinding.backBtn.setOnClickListener {
                val intent = Intent()
                setResult(RESULT_CANCELED, intent)
                finish()
            }
            mpQuizSetAttrBinding.saveBtn.setOnClickListener {
                val intentBack = Intent()
                val titleText = mpQuizSetAttrBinding.QuizTitle.text.toString()
                intentBack.putExtra("Key_title", titleText)
                intentBack.putExtra("Key_startDateTime", quizStartDateTime)
                intentBack.putExtra("Key_endDateTime", quizEndDateTime)
                intentBack.putStringArrayListExtra("Key_members", quizMembers)
                setResult(RESULT_OK, intentBack)
                finish()
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
        else{
            spQuizSetAttrBinding.backBtn.setOnClickListener {
                val intent = Intent()
                setResult(RESULT_CANCELED, intent)
                finish()
            }
            spQuizSetAttrBinding.saveBtn.setOnClickListener {
                val intentBack = Intent()
                val titleText = spQuizSetAttrBinding.QuizTitle.text.toString()
                val duringTimeMinStr : String = spQuizSetAttrBinding.QuizDuringTimeMin.text.toString()
                val duringTimeSecStr : String = spQuizSetAttrBinding.QuizDuringTimeSec.text.toString()
                val duringTimeMin = duringTimeMinStr.toInt()
                val duringTimeSec = duringTimeSecStr.toInt()
                if(duringTimeMin>200 || duringTimeMin == 0 || duringTimeSec>59){
                    AlertDialog.Builder(this).setTitle("考試時長設定有誤!").setPositiveButton("我懂", null).show()
                }
                intentBack.putExtra("Key_title", titleText)
                intentBack.putExtra("Key_startDateTime", quizStartDateTime)
                intentBack.putExtra("Key_endDateTime", quizEndDateTime)
                intentBack.putExtra("Key_duringTime", duringTimeMin*60 + duringTimeSec )
                setResult(RESULT_OK, intentBack)
                finish()
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

        if(type=="casual") {
            mpQuizSetAttrBinding = ActivityMpSingleQuizSettingBinding.inflate(layoutInflater)
            setContentView(mpQuizSetAttrBinding.root)
            val members = intent.getStringArrayListExtra("Key_members")
            var MembersStr = "無成員"

            if (members != null)
                this.quizMembers = members
            if (members != null) {
                if(members.size>0) {
                    MembersStr = ""
                    for(item in members){
                        MembersStr += item
                        MembersStr += " "
                    }
                }
            }
            mpQuizSetAttrBinding.QuizMembers.text = MembersStr
            mpQuizSetAttrBinding.QuizTitle.setText(title)
            mpQuizSetAttrBinding.QuizStatus.text = status
            mpQuizSetAttrBinding.QuizStartDate.text = startDateTime
            mpQuizSetAttrBinding.QuizEndDate.text = endDateTime
        }
        else
        {
            spQuizSetAttrBinding = ActivitySpSingleQuizSettingBinding.inflate(layoutInflater)
            setContentView(spQuizSetAttrBinding.root)
            val duringTime = intent.getIntExtra("Key_duringTime", 0)
            quizDuringTime = duringTime
            spQuizSetAttrBinding.QuizTitle.setText(title)
            spQuizSetAttrBinding.QuizStatus.text = status
            spQuizSetAttrBinding.QuizStartDate.text = startDateTime
            spQuizSetAttrBinding.QuizEndDate.text = endDateTime
            spQuizSetAttrBinding.QuizDuringTimeMin.setText((duringTime/60).toString())
            spQuizSetAttrBinding.QuizDuringTimeSec.setText((duringTime%60).toString())
        }
    }
}

