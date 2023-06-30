package com.example.test.Activity.MultiQuiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.MpSingleQuizSettingBinding

class SingleQuizSetting: AppCompatActivity() {
    private lateinit var quizSetAttrBinding: MpSingleQuizSettingBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDate: String
    private lateinit var quizEndDate: String
    private lateinit var quizMembers: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizSetAttrBinding = MpSingleQuizSettingBinding.inflate(layoutInflater)
        setContentView(quizSetAttrBinding.root)
//        fragM = supportFragmentManager
        init()

        quizSetAttrBinding.backBtn.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        quizSetAttrBinding.saveBtn.setOnClickListener {
            val intent = Intent()
            val titelText = quizSetAttrBinding.QuizTitle.text.toString()
            intent.putExtra("Key_title", titelText)
            intent.putExtra("Key_startDate", quizStartDate)
            intent.putExtra("Key_endDate", quizEndDate)
            intent.putStringArrayListExtra("Key_members", quizMembers)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
    private fun init(){
        val title = intent.getStringExtra("Key_title")
        val status = intent.getStringExtra("Key_status")
        val startDate = intent.getStringExtra("Key_startDate")
        val endDate = intent.getStringExtra("Key_endDate")
        val members = intent.getStringArrayListExtra("Key_members")
        var MembersStr = ""
        if (members != null) {
            if(members.size>0) {
                for(item in members){
                    MembersStr += item
                    MembersStr += " "
                }
            }
            else {
                MembersStr = "無成員"
            }
        }

        if (title != null)
            this.quizTitle = title
        if (status != null)
            this.quizStatus = status
        if (startDate != null)
            this.quizStartDate = startDate
        if (endDate != null)
            this.quizEndDate = endDate
        if (members != null)
            this.quizMembers = members

        quizSetAttrBinding.QuizTitle.setText(title)
        quizSetAttrBinding.QuizStatus.text = status
        quizSetAttrBinding.QuizStartDate.text = startDate
        quizSetAttrBinding.QuizEndDate.text = endDate
        quizSetAttrBinding.QuizMembers.text = MembersStr
    }
}