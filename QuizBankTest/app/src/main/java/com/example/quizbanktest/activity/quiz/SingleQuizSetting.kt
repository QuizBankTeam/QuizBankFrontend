package com.example.quizbanktest.activity.quiz
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.databinding.ActivityMpSingleQuizSettingBinding
import com.example.quizbanktest.databinding.ActivitySpSingleQuizSettingBinding
import com.example.quizbanktest.R

class SingleQuizSetting: AppCompatActivity() {
    private lateinit var mpQuizSetAttrBinding: ActivityMpSingleQuizSettingBinding
    private lateinit var spQuizSetAttrBinding: ActivitySpSingleQuizSettingBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDate: String
    private lateinit var quizEndDate: String
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
                val intent = Intent()
                val titelText = mpQuizSetAttrBinding.QuizTitle.text.toString()
                intent.putExtra("Key_title", titelText)
                intent.putExtra("Key_startDate", quizStartDate)
                intent.putExtra("Key_endDate", quizEndDate)
                intent.putStringArrayListExtra("Key_members", quizMembers)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        else{
            spQuizSetAttrBinding.backBtn.setOnClickListener {
                val intent = Intent()
                setResult(RESULT_CANCELED, intent)
                finish()
            }
            spQuizSetAttrBinding.saveBtn.setOnClickListener {
                val intent = Intent()
                val titleText = spQuizSetAttrBinding.QuizTitle.text.toString()
                val duringTimeMinStr : String = spQuizSetAttrBinding.QuizDuringTimeMin.text.toString()
                val duringTimeSecStr : String = spQuizSetAttrBinding.QuizDuringTimeSec.text.toString()
                val duringTimeMin = duringTimeMinStr.toInt()
                val duringTimeSec = duringTimeSecStr.toInt()
                if(duringTimeMin>200 || duringTimeMin == 0 || duringTimeSec>59){
                    AlertDialog.Builder(this).setTitle("考試時長設定有誤!").setPositiveButton("我懂", null).show()
                }
                intent.putExtra("Key_title", titleText)
                intent.putExtra("Key_startDate", quizStartDate)
                intent.putExtra("Key_endDate", quizEndDate)
                intent.putExtra("Key_duringTime", duringTimeMin*60 + duringTimeSec )
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
    private fun init(){
        val title = intent.getStringExtra("Key_title")
        val status = intent.getStringExtra("Key_status")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
        var type = intent.getStringExtra("Key_type")

        if (title != null)
            this.quizTitle = title
        if (status != null)
            this.quizStatus = status
        if (startDate != null)
            this.quizStartDate = startDate
        if (endDate != null)
            this.quizEndDate = endDate
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
            mpQuizSetAttrBinding.QuizStartDate.text = startDate
            mpQuizSetAttrBinding.QuizEndDate.text = endDate
        }
        else
        {
            spQuizSetAttrBinding = ActivitySpSingleQuizSettingBinding.inflate(layoutInflater)
            setContentView(spQuizSetAttrBinding.root)
            val duringTime = intent.getIntExtra("Key_duringTime", 0)
            quizDuringTime = duringTime
            spQuizSetAttrBinding.QuizTitle.setText(title)
            spQuizSetAttrBinding.QuizStatus.text = status
            spQuizSetAttrBinding.QuizStartDate.text = startDate
            spQuizSetAttrBinding.QuizEndDate.text = endDate
            spQuizSetAttrBinding.QuizDuringTimeMin.setText((duringTime/60).toString())
            spQuizSetAttrBinding.QuizDuringTimeSec.setText((duringTime%60).toString())
        }
    }
}

