package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.SyncStateContract.Constants
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.databinding.ActivityMpStartQuizBinding
import com.example.quizbanktest.fragment.SingleQuizPage
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class  MPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: ActivityMpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private var quizIndex: Int = 0
    private lateinit var optionAdapter: OptionAdapter
    private lateinit var casualDuringTime: ArrayList<Int>
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var questionList : ArrayList<Question>
    private lateinit var userAnsOptions : ArrayList<ArrayList<String>>
    private lateinit var userAnsDescription : ArrayList<String>
    private var currentAtQuestion: Int = 0
    private var currentSelection = ArrayList<Int>() //被選過的option
    private var selectedView = ArrayList<View>()  //被選過的option的background index和currentSelection 相同
    private var trueOrFalseView: View? = null
    private var trueOrFalseSelected = false
    private var currentAnswer : String = ""
    private lateinit var countDownTimer: CountDownTimer
    private val roomNumber:Int = (100000 .. 999999).random()
    private val randomList = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffled()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = ActivityMpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()


        val builder = AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle)
        val v:View =  layoutInflater.inflate(R.layout.dialog_mp_quiz_start_lobby, null)

//        builder.setTitle("")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val quizStart:TextView = v.findViewById(R.id.Quiz_start)
        val roomNumberTextView:TextView  = v.findViewById(R.id.room_number)
        val quizMembers: TextView = v.findViewById(R.id.Quiz_members)


        quizMembers.text = com.example.quizbanktest.utils.Constants.userId + "(你)"

        roomNumberTextView.text = roomNumber.toString()
        quizStart.setOnClickListener {
            dialog.dismiss()
            setQuestion()
        }

        startQuizBinding.exitQuiz.setOnClickListener {
            exitQuiz()
        }

    }
    private fun init(){
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_quizTitle")
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val type = intent.getStringExtra("Key_type")
        val quizIndex = intent.getIntExtra("quiz_index", 0)
        val startDateTime = intent.getStringExtra("Key_startDateTime")
        val endDateTime = intent.getStringExtra("Key_endDateTime")
        val members = intent.getStringArrayListExtra("Key_members")
        val casualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")

        if (id != null) {
            quizId = id
        }
        if (title != null) {
            quizTitle = title
        }
        if (questions != null) {
            questionList = questions
        }
        if (type != null) {
            quizType = type
        }
        if (startDateTime != null) {
            quizStartDateTime = startDateTime
        }
        if (endDateTime != null) {
            quizEndDateTime = endDateTime
        }
        if (members != null) {
            quizMembers = members
        }
        if (casualDuringTime != null) {
            this.casualDuringTime = casualDuringTime
        }
        this.quizIndex = quizIndex

        startQuizBinding.progressBar.progress = 1
        startQuizBinding.progressBar.max = questionList.size
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionList.size.toString()
        userAnsOptions =  ArrayList<ArrayList<String>>(questions!!.size)
        userAnsDescription = ArrayList<String>(questions.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun setQuestion(){
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
        val currentQuestion = questionList[currentAtQuestion]
        val optionlist : ArrayList<Option> = ArrayList()


        startQuizBinding.questionDescription.text = currentQuestion.description
        currentSelection.clear()
        setQuestionProgress()

        startQuizBinding.QuestionType.text = if(currentQuestion.questionType=="MultipleChoiceS") "單選題"
        else if(currentQuestion.questionType=="MultipleChoiceM") "多選題"
        else if(currentQuestion.questionType=="TrueOrFalse") "是非題"
        else if(currentQuestion.questionType=="ShortAnswer") "簡答題"
        else "填充題"

        val imageArr = ArrayList<Bitmap>()
        for(item in SingleQuizPage.Companion.quizListImages[quizIndex][currentAtQuestion]){
            val imageBytes: ByteArray = Base64.decode(item, Base64.DEFAULT)
            val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageArr.add(decodeImage)

        }
        if(imageArr.isNotEmpty())
            startQuizBinding.QuestionImage.setImageBitmap(imageArr[0])



        when(currentQuestion.questionType){
            "MultipleChoiceS",  "MultipleChoiceM"->{
                if(this.trueOrFalseView!=null)
                    trueOrFalseView!!.visibility = View.GONE

                for(index in currentQuestion.options?.indices!!){
                    val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
                    optionlist.add(tmpOption)
                }
                startQuizBinding.QuestionOption.visibility = View.VISIBLE
                this.optionAdapter = OptionAdapter(this, optionlist)
                startQuizBinding.QuestionOption.adapter = this.optionAdapter
                startQuizBinding.QuestionOption.layoutManager = LinearLayoutManager(this)
                startQuizBinding.QuestionOption.setHasFixedSize(true)
                optionAdapter.setSelectClickListener(object: OptionAdapter.SelectOnClickListener{
                    override fun onclick(position: Int, holder: OptionAdapter.MyViewHolder) {
                        optionSelect(position, holder, currentQuestion.questionType!!)
                    }
                })
            }
            "TrueOrFalse" -> {
                startQuizBinding.QuestionOption.visibility = View.GONE
                initTrueOrFalse()
            }
        }
    }
    private fun setQuestionProgress(){
        startQuizBinding.progressBar.max = casualDuringTime[currentAtQuestion]*100
        startQuizBinding.progressBar.progress = casualDuringTime[currentAtQuestion]
        startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionList.size.toString()
        countDownTimer = object : CountDownTimer((casualDuringTime[currentAtQuestion]*1000).toLong(), 50) {
            override fun onFinish() {
                questionSubmit()
                if(currentAtQuestion == questionList.size-1) {
                    quizEnd()
                }else{
                    currentAtQuestion+=1
                    setQuestion()
                }
            }
            override fun onTick(millisUntilFinished: Long) {
                val totalRemain = millisUntilFinished/10
                startQuizBinding.progressBar.progress = totalRemain.toInt()
            }
        }.start()
    }
    private fun optionSelect(position: Int, holder: OptionAdapter.MyViewHolder, type: String){
        val currentQuestion = questionList[currentAtQuestion]

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
                    currentSelection.remove(position)
                    selectedView.remove(holder.optionBackground)
                    holder.optionBackground.setBackgroundResource(0)
                }else{           // 被選的選項沒被選過
                    currentSelection.add(position)
                    selectedView.add(holder.optionBackground)
                    holder.optionBackground.background = ContextCompat.getDrawable(this, R.drawable.select_option_border)
                }
            }
        }
    }

    private fun questionSubmit(){
        val currentQuestion = questionList[currentAtQuestion]
        val addUserAns = ArrayList<String>(currentSelection.size)
        Log.d("in question submit", "")

        when(currentQuestion.questionType){
            "MultipleChoiceS", "MultipleChoiceM" -> {
                val options = currentQuestion.options!!
                if(currentSelection.isNotEmpty()) {
                    for(i in currentSelection){
                        addUserAns.add(options[i])
                    }
                }
                selectedView.clear()
                currentSelection.clear()
                userAnsOptions.add(addUserAns)
                userAnsDescription.add("")
            }
            "TrueOrFalse" -> {

                if(trueOrFalseSelected) {
                    addUserAns.add(currentAnswer)
                }else{
                    addUserAns.add("")
                }
                userAnsOptions.add(addUserAns)
                userAnsDescription.add("")
            }
        }
    }

    private fun quizEnd(){
        val intent = Intent()

        for(i in currentAtQuestion until questionList.size){
            val tmpAnsOptions = ArrayList<String>()
            userAnsOptions.add(tmpAnsOptions)
            userAnsDescription.add("")
        }
        intent.setClass(this, MPQuizFinish::class.java)
        intent.putExtra("Key_userAnsOptions", userAnsOptions)
        intent.putStringArrayListExtra("Key_userAnsDescription", userAnsDescription)
        intent.putExtra("Key_id", quizId)
        intent.putExtra("Key_title", quizTitle)
//        intent.putExtra("Key_startDate", startDateStr)
//        intent.putExtra("Key_endDate", endDateStr)
        intent.putExtra("Key_type", quizType)
        intent.putParcelableArrayListExtra("Key_questions", questionList)
        startActivityForResult(intent, 1000)
    }

    private fun initTrueOrFalse(){
        trueOrFalseSelected = false
        if(trueOrFalseView!=null){
            this.trueOrFalseView!!.visibility = View.VISIBLE
        }else{
            val v: View =  layoutInflater.inflate(R.layout.item_option_trueorfalse, startQuizBinding.startQuizContainer, false)
            val vHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics).toInt()
            val tfParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, vHeight)
            val textViewTrue : TextView = v.findViewById(R.id.option_true)
            val textViewFalse: TextView = v.findViewById(R.id.option_false)
            tfParams.addRule(RelativeLayout.BELOW, startQuizBinding.questionDescription.id)
            v.layoutParams = tfParams
            v.id = View.generateViewId()

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
            startQuizBinding.lowerContainer.addView(v, 1)
            this.trueOrFalseView = v
        }
    }
    private fun exitQuiz(){
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("確定退出考試?")
        builder.setPositiveButton("確定") { dialog, which ->
            finish()
        }
        builder.setNegativeButton("取消", null)
        builder.show()
    }
    override fun onBackPressed() {
        exitQuiz()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun doubleCheckExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                exitQuiz()
            }
        }
    }
}