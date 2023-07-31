package com.example.quizbanktest.activity.quiz
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.databinding.ActivitySpStartQuizBinding
import com.example.quizbanktest.fragment.SingleQuizPage
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord

class SPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: ActivitySpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var userAnsOptions : ArrayList<ArrayList<String>>
    private lateinit var userAnsDescription : ArrayList<String>
    private lateinit var optionAdapter: OptionAdapter
    private var currentAnswer : String = ""
    private var duringTime: Int = 0
    private var currentAtQuestion: Int = 0
    private var currentSelection = ArrayList<Int>() //被選過的option
    private var selectedView = ArrayList<View>()  //被選過的option的background index和currentSelection 相同
    private lateinit var startDate: LocalDateTime
    private var shortAnswerView : EditText? = null
    private var trueOrFalseView: View? = null
    private var trueOrFalseSelected = false
    private var quizIndex = 0
    private var imageArr = ArrayList<Bitmap>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = ActivitySpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()
        setQuestion()
        setTimer(this)
        startDate = LocalDateTime.now()

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
        val type = intent.getStringExtra("Key_type")
        val quizIndex = intent.getIntExtra("quiz_index", 0)

        if (id != null) {
            quizId = id
        }
        if (title != null) {
            quizTitle = title
        }
        if (type != null) {
            quizType = type
        }
        if (questions != null) {
            questionlist = questions
        }

        this.duringTime = duringTime
        this.quizIndex = quizIndex
        startQuizBinding.progressBar.progress = 1
        startQuizBinding.progressBar.max = questionlist.size
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionlist.size.toString()
        userAnsOptions =  ArrayList<ArrayList<String>>(questions!!.size)
        userAnsDescription = ArrayList<String>(questions.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in start quiz request code is", requestCode.toString())
        if(resultCode == RESULT_OK){
            if(data!=null) {
                val questionRecordList =
                    data.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
                val quizRecord = data.getParcelableExtra<QuizRecord>("Key_quizRecord")
                val intent = Intent()
                intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
                intent.putExtra("Key_quizRecord", quizRecord)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Log.d("data is null", "")
            }
        }else if(resultCode == RESULT_CANCELED){
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setQuestion(){
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
        val currentQuestion = questionlist[currentAtQuestion]
        val optionlist : ArrayList<Option> = ArrayList()


        startQuizBinding.questionDescription.text = currentQuestion.description
        currentSelection.clear()
        startQuizBinding.progressBar.progress = currentAtQuestion+1
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionlist.size.toString()
        startQuizBinding.QuestionType.text = if(currentQuestion.questionType=="MultipleChoiceS") "單選題"
                                        else if(currentQuestion.questionType=="MultipleChoiceM") "多選題"
                                        else if(currentQuestion.questionType=="TrueOrFalse") "是非題"
                                        else if(currentQuestion.questionType=="ShortAnswer") "簡答題"
                                        else "填充題"

        val imageArr = ArrayList<Bitmap>()
        for(item in SingleQuizPage.Companion.quizListImages[quizIndex][currentAtQuestion]){
            val tmpImageStr: String? = item.get()
            if(tmpImageStr!=null) {
                val imageBytes: ByteArray = Base64.decode(tmpImageStr, Base64.DEFAULT)
                val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageArr.add(decodeImage)
            }
        }
        if(imageArr.isNotEmpty())
            startQuizBinding.QuestionImage.setImageBitmap(imageArr[0])



        when(currentQuestion.questionType){
            "MultipleChoiceS",  "MultipleChoiceM"->{
                if(this.shortAnswerView!=null)
                    shortAnswerView!!.visibility = View.GONE
                if(this.trueOrFalseView!=null)
                    trueOrFalseView!!.visibility = View.GONE

                for(index in currentQuestion.options?.indices!!){
                    val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
                    optionlist.add(tmpOption)
                }
                startQuizBinding.QuestionOption.visibility = View.VISIBLE
                optionAdapter = OptionAdapter(this, optionlist)
                startQuizBinding.QuestionOption.adapter = optionAdapter
                startQuizBinding.QuestionOption.layoutManager = LinearLayoutManager(this)
                startQuizBinding.QuestionOption.setHasFixedSize(true)
                optionAdapter.setSelectClickListener(object: OptionAdapter.SelectOnClickListener{
                    override fun onclick(position: Int, holder: OptionAdapter.MyViewHolder) {
                        optionSelect(position, holder, currentQuestion.questionType!!)
                    }
                })
            }
            "TrueOrFalse" -> {
                if(this.shortAnswerView!=null)
                    shortAnswerView!!.visibility = View.GONE
                startQuizBinding.QuestionOption.visibility = View.GONE
                initTrueOrFalse()
            }
            "ShortAnswer" -> {
                if(this.trueOrFalseView!=null)
                    this.trueOrFalseView!!.visibility = View.GONE
                startQuizBinding.QuestionOption.visibility = View.GONE
                initShortAnswer()
            }
        }


    }

    private fun optionSelect(position: Int, holder: OptionAdapter.MyViewHolder, type: String){
        val currentQuestion = questionlist[currentAtQuestion]

        when(type){ //多選 單選
            "MultipleChoiceS" -> {
                if (currentSelection.isNotEmpty()) { // 有選項被選過
                    if(position==currentSelection[0]) {  // 被選過的選項又被選了一次
                        holder.optionBackground.setBackgroundResource(0)
                        currentSelection.clear()
                        selectedView.clear()
                    }
                    else{                          // 被選的選項 和原來的不同
                        currentSelection.clear()
                        currentSelection.add(position)
                        selectedView[0].setBackgroundResource(0)
                        selectedView.clear()
                        selectedView.add(holder.optionBackground)
                        holder.optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                    }
                }
                else{  //目前沒有選項被選過
                    currentSelection.add(position)
                    selectedView.add(holder.optionBackground)
                    holder.optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                }
            }
            "MultipleChoiceM" -> {
                if( currentSelection.contains(position) ){    // 被選過的選項又被選了一次
                    holder.optionBackground.setBackgroundResource(0)
                    currentSelection.remove(position)
                    selectedView.remove(holder.optionBackground)
                }else{           // 被選的選項沒被選過
                    currentSelection.add(position)
                    selectedView.add(holder.optionBackground)
                    holder.optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                }
            }
        }
    }

    private fun questionSubmit(){
        val currentQuestion = questionlist[currentAtQuestion]
        val addUserAns = ArrayList<String>()
        var readyToEnd = false
        var gotoNext = true
        Log.d("in question submit", "")

        when(currentQuestion.questionType){
            "MultipleChoiceS", "MultipleChoiceM" -> {
                val options = currentQuestion.options!!
                if(currentSelection.isEmpty()) {
                    Toast.makeText(this, "請至少選擇一個選項", Toast.LENGTH_LONG).show()
                    gotoNext = false
                }else{
                    val tmpAdd = ArrayList<String>(currentSelection.size)
                    for(i in currentSelection){
                        tmpAdd.add(options[i])
                    }
                    userAnsOptions.add( tmpAdd )
                    userAnsDescription.add("")
                    selectedView.clear()
                    currentSelection.clear()
                }

            }
            "ShortAnswer" -> {
                Log.d("short ans is", this.shortAnswerView?.text.toString())
                userAnsOptions.add( addUserAns )
                userAnsDescription.add(this.shortAnswerView?.text.toString())
            }
            "TrueOrFalse" -> {
                if(trueOrFalseSelected) {
                    addUserAns.add(currentAnswer)
                    userAnsOptions.add(addUserAns)
                    userAnsDescription.add("")

                }else{
                    Toast.makeText(this, "請選擇一個選項", Toast.LENGTH_LONG).show()
                    gotoNext = false
                }
            }
        }

        if(currentAtQuestion == questionlist.size-1) {
            quizEnd()
        }
        else if(gotoNext){
            currentAtQuestion += 1
            setQuestion()
            if (currentAtQuestion == questionlist.size - 1) {
                startQuizBinding.btnSubmit.text = "考試結束"
            }
        }
    }

    private fun quizEnd(){
        val answerlist = ArrayList<ArrayList<String>>()
//        for(item in answerRecords){
//            val first = ArrayList<String>(item.size)
//            first.addAll(item)
//            answerlist.add(first)
//        }
//        var questionlist1  = ArrayList<Question>(questionlist.size)
//        for(item in questionlist) {
//            questionlist1.add(item)
//            item.number?.let { Log.d("question", it) }
//        }
        val intent = Intent()
        val endDate = LocalDateTime.now()
        val duringTime = java.time.Duration.between(startDate, endDate).toMinutes().toInt()*60
        val startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
        val endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))

        for(i in currentAtQuestion until questionlist.size){
            val tmpAnsOptions = ArrayList<String>()
            userAnsOptions.add(tmpAnsOptions)
            userAnsDescription.add("")
        }
        intent.setClass(this, SPQuizFinish::class.java)
        intent.putExtra("Key_userAnsOptions", userAnsOptions)
        intent.putStringArrayListExtra("Key_userAnsDescription", userAnsDescription)
        intent.putExtra("Key_id", quizId)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_startDate", startDateStr)
        intent.putExtra("Key_endDate", endDateStr)
        intent.putExtra("Key_type", quizType)
        intent.putExtra("Key_duringTime", duringTime)
        intent.putParcelableArrayListExtra("Key_questions", questionlist)
        startActivityForResult(intent, 1000)
    }

    private fun setTimer(currentContext: Context){
        object : CountDownTimer((duringTime*1000).toLong(), 1000) {
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

    private fun initShortAnswer(){
        if(this.shortAnswerView!=null){
            this.shortAnswerView!!.visibility = View.VISIBLE
            Log.d("answerDesc is not null", "")
        }else{
            val textView = EditText(this)
            val answerMinH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150f, resources.displayMetrics).toInt()
            val descriptionMinH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources.displayMetrics).toInt()
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
            val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics)
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            val marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
            val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParam.marginStart = margin
            layoutParam.marginEnd = margin
            layoutParam.topMargin = marginTop
            layoutParam.bottomMargin = marginTop
            textView.id = View.generateViewId()

            textView.setBackgroundResource(R.drawable.textview_inquiz_border)
            textView.setPadding(padding)
            textView.layoutParams = layoutParam
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.textSize = textSize
            textView.minHeight = answerMinH
            textView.hint = "輕觸輸入答案"
            startQuizBinding.startQuizContainer.addView(textView, 4)
            startQuizBinding.questionDescription.minHeight = descriptionMinH
            val answerDescriptionView = startQuizBinding.root.findViewById<EditText>(textView.id)
            this.shortAnswerView = answerDescriptionView
        }

    }

    private fun initTrueOrFalse(){
        trueOrFalseSelected = false
        if(trueOrFalseView!=null){
            this.trueOrFalseView!!.visibility = View.VISIBLE
        }else{
            val v:View =  layoutInflater.inflate(R.layout.item_option_trueorfalse, startQuizBinding.startQuizContainer, false)
            val textViewTrue : TextView = v.findViewById(R.id.option_true)
            val textViewFalse: TextView = v.findViewById(R.id.option_false)
            textViewTrue.setOnClickListener {
                textViewTrue.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                textViewFalse.setBackgroundResource(0)
                this.currentAnswer = "true"
                trueOrFalseSelected = true
            }
            textViewFalse.setOnClickListener {
                textViewTrue.setBackgroundResource(0)
                textViewFalse.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                this.currentAnswer  = "false"
                trueOrFalseSelected = true
            }
            startQuizBinding.startQuizContainer.addView(v, 4)
            this.trueOrFalseView = v
        }
    }
}