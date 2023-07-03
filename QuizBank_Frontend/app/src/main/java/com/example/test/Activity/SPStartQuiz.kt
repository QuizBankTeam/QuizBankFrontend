package com.example.test.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test.Adapter.OptionAdapter
import com.example.test.R
import com.example.test.databinding.SpStartQuizBinding
import com.example.test.model.Option
import com.example.test.model.Question
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: SpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var questionlist : ArrayList<Question>
    private var answerRecords = ArrayList<ArrayList<Int>>()
    private lateinit var optionAdapter: OptionAdapter
    private var duringTime: Int = 0
    private var currentAtQuestion: Int = 0
    private var currentSelection = ArrayList<Int>() //被選過的option
    private var selectedView = ArrayList<View>()  //被選過的option的background index和currentSelection 相同
    private lateinit var startDate: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = SpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()
        setQuestion()
        setTimer(this)
        startDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh:mm:ss"))
        startQuizBinding.QuestionOption.setOnItemClickListener { parent, view, position, id ->
            optionSelect(position, view)
        }

        startQuizBinding.btnSubmit.setOnClickListener {
            questionSubmit()
        }

        startQuizBinding.exitQuiz.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("確定退出考試?")
            builder.setPositiveButton("確定") { dialog, which ->
                finish()
            }
            builder.setNegativeButton("取消", null)
            builder.show()
        }

    }
    private fun init(){
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_quizTitle")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")

        if (id != null) {
            quizId = id
        }
        if (title != null) {
            quizTitle = title
        }
        if (questions != null) {
            questionlist = questions
//            val intent = Intent()
//            intent.setClass(this, SPQuizFinish::class.java)
//            intent.putParcelableArrayListExtra("Key_questions", questionlist)
//            startActivity(intent)
        }

        this.duringTime = duringTime
        startQuizBinding.progressBar.progress = 1
        startQuizBinding.progressBar.max = questionlist.size
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionlist.size.toString()

    }

    private fun setQuestion(){
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F")
        val currentQuestion = questionlist[currentAtQuestion]
        val optionlist : ArrayList<Option> = ArrayList()
        if(currentQuestion.type=="MultipleChoiceS"){
            startQuizBinding.QuestionType.text = "單選題"
        }
        else if(currentQuestion.type=="MultipleChoiceM"){
            startQuizBinding.QuestionType.text = "多選題"
        }
        for(index in currentQuestion.options?.indices!!){
            val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
            optionlist.add(tmpOption)
        }
        startQuizBinding.remainTime.text = duringTime.toString()
        optionAdapter = OptionAdapter(this, optionlist)
        optionAdapter.setInStartQuiz(true)
        startQuizBinding.QuestionOption.adapter = optionAdapter
        startQuizBinding.questionDescription.text = currentQuestion.description
        startQuizBinding.QuestionImage.setImageResource(currentQuestion.image)
        currentSelection.clear()
        startQuizBinding.progressBar.progress = currentAtQuestion+1
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionlist.size.toString()
    }

    private fun optionSelect(position: Int, view: View){
        val optionBackground: LinearLayout = view.findViewById(R.id.option_background)
        val currentQuestion = questionlist[currentAtQuestion]
        Log.d("in option select", "")

        if(currentQuestion.type=="MultipleChoiceS") { //單選
            if (currentSelection.isNotEmpty()) { // 有選項被選過
                if(position==currentSelection[0]) {  // 被選過的選項又被選了一次
                    selectedView[0].setBackgroundResource(0)
                    currentSelection.clear()
                    selectedView.clear()
                }
                else{                          // 被選的選項 和原來的不同
                    currentSelection.clear()
                    currentSelection.add(position)
                    selectedView[0].setBackgroundResource(0)
                    selectedView.clear()
                    selectedView.add(optionBackground)
                    optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                }
            }
            else{  //目前沒有選項被選過
                currentSelection.add(position)
                selectedView.add(optionBackground)
                optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
            }
        }
        else if(currentQuestion.type=="MultipleChoiceM"){   //多選
            var selected = false
            for(index in currentSelection.indices){
                if(position == currentSelection[index]){    // 被選過的選項又被選了一次
                    selectedView[index].setBackgroundResource(0)
                    currentSelection.removeAt(index)
                    selectedView.removeAt(index)
                    selected = true
                    break
                }
            }
            if(!selected){  // 被選的選項沒被選過
                currentSelection.add(position)
                selectedView.add(optionBackground)
                optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun questionSubmit(){
        val currentQuestion = questionlist[currentAtQuestion]
        val options = currentQuestion.options!!
        if(currentSelection.isNotEmpty()){
            if( currentAtQuestion < questionlist.size )
            {
                val tmpAdd = ArrayList<Int>(currentSelection.size)
                for(i in currentSelection){
                    tmpAdd.add(i)
                }
                answerRecords.add( tmpAdd )

                if(currentAtQuestion == questionlist.size-1)
                {
                    quizEnd()
                }
                else
                {
                    selectedView.clear()
                    currentSelection.clear()
                    currentAtQuestion += 1
                    setQuestion()
                    if (currentAtQuestion == questionlist.size - 1) {
                        startQuizBinding.btnSubmit.text = "考試結束"
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "請至少選擇一個選項", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun quizEnd(){
        val answerlist = ArrayList<ArrayList<Int>>()
        for(item in answerRecords){
            val first = ArrayList<Int>(item.size)
            first.addAll(item)
            answerlist.add(first)
        }
        var questionlist1  = ArrayList<Question>(questionlist.size)
        for(item in questionlist) {
            questionlist1.add(item)
            item.number?.let { Log.d("question", it) }
        }
        val intent = Intent()
        val endDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh:mm:ss"))
        intent.setClass(this, SPQuizFinish::class.java)
        intent.putExtra("Key_answerRecords", answerlist)
        intent.putExtra("Key_id", quizId)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_startDate", startDate)
        intent.putExtra("Key_endDate", endDate)
        intent.putParcelableArrayListExtra("Key_questions", questionlist1)
        startActivity(intent)
    }

    private fun setTimer(currentContext: Context){
        object : CountDownTimer((duringTime*1000).toLong(), 1000) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                val builder = AlertDialog.Builder(currentContext)
                builder.setTitle("考試已結束")
                builder.setPositiveButton("確定") { dialog, which ->
                    quizEnd()
                }
                builder.show()
            }

            override fun onTick(millisUntilFinished: Long) {
                val totalRemain = millisUntilFinished/1000
                val remainMin = totalRemain/60
                var remainSec = (totalRemain%60).toInt()
                var remainSecStr = remainSec.toString()
                if(remainSec<10){
                    if(remainSec==0){
                        remainSecStr = "00"
                    }else{
                        remainSecStr = "0" + remainSec.toString()
                    }
                }
                startQuizBinding.remainTime.text = remainMin.toString() + ":" + remainSecStr

            }
        }.start()
    }
}