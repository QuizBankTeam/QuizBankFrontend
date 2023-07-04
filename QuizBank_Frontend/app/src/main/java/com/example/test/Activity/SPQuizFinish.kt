package com.example.test.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.test.Adapter.OptionAdapter
import com.example.test.R
import com.example.test.databinding.SpQuizFinishBinding
import com.example.test.model.Option
import com.example.test.model.Question
import com.example.test.model.QuestionRecord
import com.example.test.model.QuizRecord
import java.util.UUID

class SPQuizFinish : AppCompatActivity(){
    private lateinit var finishQuizBinding: SpQuizFinishBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var answerRecords: ArrayList< ArrayList<Int> >
    private lateinit var quizId: String
    private lateinit var quizType: String
    private lateinit var quizTitle: String
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var questionRecordList: ArrayList<QuestionRecord>
    private lateinit var quizRecord: QuizRecord
    private var questionSize = ""
    private var duringTime: Int = 0
    private var correctNum: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishQuizBinding = SpQuizFinishBinding.inflate(layoutInflater)
        setContentView(finishQuizBinding.root)

        init()
        makeRecords()
        questionSize = questionlist.size.toString()
        finishQuizBinding.correctNum.text = "你答對了 " + correctNum.toString()+ " / " + questionSize + " 題 !"
        //確定答案是否正確
        for(index in questionlist.indices){
            if(questionlist[index].type=="ShortAnswer"){
                setAnswerQuestion(questionlist[index], index)
            }
        }

        finishQuizBinding.gotoHome.setOnClickListener {
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

        //傳送questionRecordList quizRecord
        finishQuizBinding.gotoRecord.setOnClickListener {
            val intent = Intent()
            intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
            intent.putExtra("Key_quizRecord", quizRecord)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //傳送questionRecordList quizRecord
    }

    private fun init(){
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val answerRecords  = intent.getSerializableExtra("Key_answerRecords") as ArrayList<ArrayList<Int>>?
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
        if (answerRecords != null) {
            this.answerRecords = answerRecords
        }
    }

    private fun makeRecords(){
        val quizRecordId = UUID.randomUUID().toString()
        val questionRecordId = ArrayList<String>()
        var totalScore : Int = 0
        for(index in questionlist.indices){
            val tmpId = UUID.randomUUID().toString()
            val tmpAns = ArrayList<String>()
            var isCorrect = false
            questionRecordId.add(tmpId)
            if(index < answerRecords.size){
                for(item in answerRecords[index]){
                    tmpAns.add(questionlist[index].options?.get(item) ?: "nothing")
                }
            }
            else{
                tmpAns.add("user answer nothing")
            }
            isCorrect = tmpAns.toSet() == questionlist[index].answerOption!!.toSet()
            totalScore = if(isCorrect) totalScore+1 else totalScore
            Log.d(index.toString(), "is correct is"+isCorrect.toString())
//            isCorrect = tmpAns.size == answerRecords.size
//            if(isCorrect){
//                for(item in questionlist[index].answerOption!!){
//                    if(item in tmpAns)
//                        isCorrect = true
//                    else {
//                        isCorrect = false
//                        break
//                    }
//                }
//            }
            val tmpQuestionRecord = QuestionRecord(tmpId, "jacky", tmpAns, isCorrect, startDate, questionlist[index].id!!, quizRecordId)
            questionRecordList.add(tmpQuestionRecord)
        }
        correctNum = totalScore
        val tmpQuizRecord = QuizRecord(quizRecordId, quizTitle, quizId, startDate, quizType, totalScore/questionlist.size,
                                        duringTime, startDate, endDate, null, questionRecordId)
        this.quizRecord = tmpQuizRecord
    }

    private fun setAnswerQuestion(question: Question, index: Int){
        val v: View = layoutInflater.inflate(R.layout.decide_iscorrect, null)
        val answerDesc: TextView = v.findViewById(R.id.answer_description)
        val questionDesc: TextView = v.findViewById(R.id.question_description)
        val answerSwitch: SwitchCompat = v.findViewById(R.id.answer_switch)
        val isCorrectTag: TextView = v.findViewById(R.id.answer_isCorrect)
        val questionNum:  TextView = v.findViewById(R.id.question_number)

        answerDesc.text = question.answerDescription
        questionDesc.text = question.description
        questionNum.text = question.number
        answerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                isCorrectTag.text = "正確"
                isCorrectTag.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                questionRecordList[index].correct = true
                correctNum++
                finishQuizBinding.correctNum.text = "你答對了 " +correctNum.toString()+ " / " +questionSize+ " 題 !"
            }else{
                isCorrectTag.text = "錯誤"
                isCorrectTag.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_wrong))
                questionRecordList[index].correct = false
                correctNum--
                finishQuizBinding.correctNum.text = "你答對了 " +correctNum.toString()+ " / " +questionSize+ " 題 !"
            }
        }
    }
}