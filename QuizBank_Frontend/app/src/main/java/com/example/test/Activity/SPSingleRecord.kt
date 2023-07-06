package com.example.test.Activity

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.Adapter.OptionAdapter
import com.example.test.R
import com.example.test.databinding.SpSingleRecordBinding
import com.example.test.model.Option
import com.example.test.model.Question
import com.example.test.model.QuestionRecord
import com.example.test.model.QuizRecord

class SPSingleRecord: AppCompatActivity()  {
    private lateinit var singleRecordBinding: SpSingleRecordBinding
    private lateinit var questionList: ArrayList<Question>
    private lateinit var recordList: ArrayList<QuestionRecord>
    private lateinit var quizRecord: QuizRecord
    private lateinit var optionAdapter: OptionAdapter
    private var currentAtQuestion: Int = 0
    private var shortAnswerView : TextView? = null
    private var trueOrFalseView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singleRecordBinding = SpSingleRecordBinding.inflate(layoutInflater)
        setContentView(singleRecordBinding.root)

        init()
        setQuestion()
        singleRecordBinding.gotoNextQuestion.setOnClickListener {
            gotoNextQ()
        }
        singleRecordBinding.gotoPreviousQuestion.setOnClickListener {
            gotoPreviousQ()
        }
        singleRecordBinding.backBtn.setOnClickListener { finish() }
        singleRecordBinding.gotoPreviousQuestion.isEnabled = false
    }

    private fun init(){
        val spRecordList = intent.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
        val questionList = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val spQuizRecord = intent.getParcelableExtra<QuizRecord>("Key_quizRecord")

        if (questionList != null) {
            this.questionList = questionList
        }
        if (spRecordList != null) {
            this.recordList = spRecordList
        }
        if (spQuizRecord != null) {
            this.quizRecord = spQuizRecord
        }
    }

    private fun setQuestion(){
        for(item in recordList[currentAtQuestion].userAnswer!!){
            Log.d("user ans is", item)
        }
        val currentQuestion = questionList[currentAtQuestion]
        singleRecordBinding.QuestionType.text
        singleRecordBinding.QuestionType.text = if(currentQuestion.type=="MultipleChoiceS") "單選題"
                                            else if(currentQuestion.type=="MultipleChoiceM") "多選題"
                                            else if(currentQuestion.type=="TrueOrFalse") "是非題"
                                            else if(currentQuestion.type=="ShortAnswer") "簡答題"
                                            else "填充題"
        singleRecordBinding.questionDescription.text = currentQuestion.description
        singleRecordBinding.QuestionImage.setImageResource(currentQuestion.image)
        singleRecordBinding.progressBar.max = questionList.size
        singleRecordBinding.progressBar.progress = currentAtQuestion+1
        singleRecordBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionList.size.toString()

        when(currentQuestion.type){
            "MultipleChoiceS", "MultipleChoiceM" ->{
                if(this.shortAnswerView!=null)
                    shortAnswerView!!.visibility = View.GONE
                if(this.trueOrFalseView!=null)
                    trueOrFalseView!!.visibility = View.GONE
                singleRecordBinding.QuestionOption.visibility = View.VISIBLE
                setMultiChoice(currentQuestion)
            }
            "ShortAnswer" -> {
                if(this.trueOrFalseView!=null)
                    this.trueOrFalseView!!.visibility = View.GONE
                singleRecordBinding.QuestionOption.visibility = View.GONE
                setShortAns(recordList[currentAtQuestion].correct!!)
            }
            "TrueOrFalse" -> {
                if(this.shortAnswerView!=null)
                    shortAnswerView!!.visibility = View.GONE
                singleRecordBinding.QuestionOption.visibility = View.GONE
                setTrueOrFalse(recordList[currentAtQuestion].correct!!)
            }
            "Filling" -> {

            }
        }
    }

    private fun setMultiChoice(currentQuestion: Question){
        val optionlist : ArrayList<Option> = ArrayList()
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
        val answerOptions : ArrayList<Int> = ArrayList()
        val selectOption: ArrayList<Int> = ArrayList()


        for(index in currentQuestion.options?.indices!!){
            if(currentQuestion.answerOption?.contains(currentQuestion.options!![index]) == true){
                answerOptions.add(index)
            }
            if(recordList[currentAtQuestion].userAnswer?.contains(currentQuestion.options!![index]) == true){
                selectOption.add(index)
            }

            val tmpOption = Option(optionNum[index], currentQuestion.options!![index])
            optionlist.add(tmpOption)
        }
        var userAns = "你的答案: "
        for(item in selectOption){
            userAns = userAns + optionNum[item] + " "
        }
        val tmpOption = Option("", userAns)
        optionlist.add(tmpOption)
        singleRecordBinding.QuestionOption.layoutManager = LinearLayoutManager(this)
        singleRecordBinding.QuestionOption.setHasFixedSize(true)
        optionAdapter = OptionAdapter(this, optionlist)
        singleRecordBinding.QuestionOption.adapter = optionAdapter
        optionAdapter.setRecord(true, recordList[currentAtQuestion].correct!!, selectOption, answerOptions)
    }
    private fun setShortAns(isCorrect: Boolean){
        if(this.shortAnswerView!=null){
            this.shortAnswerView!!.visibility = View.VISIBLE
            Log.d("answerDesc is not null", "")
        }else{
            val textView = TextView(this)
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

            if(isCorrect)
                textView.setBackgroundResource(R.drawable.textview_answer_border)
            else
                textView.setBackgroundResource(R.drawable.textview_wrong_border)
            textView.setPadding(padding)
            textView.layoutParams = layoutParam
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.textSize = textSize
            textView.minHeight = answerMinH
            textView.text = recordList[currentAtQuestion].userAnswer?.get(0)
            textView.hint = "你的答案"
            singleRecordBinding.recordContainer.addView(textView, 4)
            singleRecordBinding.questionDescription.minHeight = descriptionMinH
            val answerDescriptionView = singleRecordBinding.root.findViewById<TextView>(textView.id)
            this.shortAnswerView = answerDescriptionView
        }

    }

    private fun setTrueOrFalse(isCorrect: Boolean){
        if(trueOrFalseView!=null){
            this.trueOrFalseView!!.visibility = View.VISIBLE
        }else{
            val v:View =  layoutInflater.inflate(R.layout.option_trueorfalse, singleRecordBinding.recordContainer, false)
            val textViewTrue : TextView = v.findViewById(R.id.option_true)
            val textViewFalse: TextView = v.findViewById(R.id.option_false)


            if(isCorrect) {
                if(questionList[currentAtQuestion].options?.get(0) =="true"){
                    textViewTrue.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewTrue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewTrue.compoundDrawablePadding = 0
                }else{
                    textViewFalse.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewFalse.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewFalse.compoundDrawablePadding = 0
                }
            }else {
                if(questionList[currentAtQuestion].options?.get(0) =="true"){
                    textViewTrue.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewFalse.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_wrong))
                    textViewFalse.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewFalse.compoundDrawablePadding = 0
                }else{
                    textViewFalse.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewTrue.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_wrong))
                    textViewTrue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewTrue.compoundDrawablePadding = 0
                }
            }
            singleRecordBinding.recordContainer.addView(v, 4)
            this.trueOrFalseView = v
        }
    }
    private fun gotoNextQ(){
        if(questionList.size>1) {
            if (currentAtQuestion == questionList.size - 2) {
                singleRecordBinding.gotoNextQuestion.isEnabled = false
            }
            currentAtQuestion++
            singleRecordBinding.gotoPreviousQuestion.isEnabled = true
            setQuestion()
        }
    }
    private fun gotoPreviousQ(){
        singleRecordBinding.gotoNextQuestion.isEnabled = true
        if( currentAtQuestion == 0 ){
            if(questionList.size==1)
                singleRecordBinding.gotoNextQuestion.isEnabled = false
        }
        else if( currentAtQuestion == 1 ){
            singleRecordBinding.gotoPreviousQuestion.isEnabled = false
        }

        currentAtQuestion--
        setQuestion()
    }
}