package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.ImageRVAdapter
import com.example.quizbanktest.adapters.quiz.ImageVPAdapter
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.databinding.ActivitySpSingleRecordBinding
import com.example.quizbanktest.fragment.SingleQuizPage
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import com.example.quizbanktest.utils.ConstantsQuizRecord
import com.google.android.material.bottomsheet.BottomSheetDialog

class SPSingleRecord: AppCompatActivity()  {
    private lateinit var singleRecordBinding: ActivitySpSingleRecordBinding
    private var questionList = ArrayList<Question>()
    private lateinit var recordList: ArrayList<QuestionRecord>
    private lateinit var quizRecord: QuizRecord
    private lateinit var optionAdapter: OptionAdapter
    private var questionImageArr = ArrayList< ArrayList<Bitmap> >()
    private var answerImageArr = ArrayList< ArrayList<Bitmap> >()
    private var currentAtQuestion: Int = 0
    private var shortAnswerView : TextView? = null
    private var trueOrFalseView: View? = null
    private  var quizIndex: Int = 0
    private val activitySingleQuiz = "SingleQuiz"
    private val activityRecordPage = "RecordPage"
    private val activityMain = "activityMain"
    private lateinit var previousActivity: String
    private lateinit var imageAdapter: ImageVPAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singleRecordBinding = ActivitySpSingleRecordBinding.inflate(layoutInflater)
        setContentView(singleRecordBinding.root)

        init()
        singleRecordBinding.gotoNextQuestion.setOnClickListener {
            gotoNextQ()
        }
        singleRecordBinding.gotoPreviousQuestion.setOnClickListener {
            gotoPreviousQ()
        }
        singleRecordBinding.backBtn.setOnClickListener {
            backBtn()
        }
        singleRecordBinding.showAnswer.setOnClickListener {
            showAnswer()
        }
        singleRecordBinding.gotoPreviousQuestion.isEnabled = false
    }

    private fun init(){

        val previousActivity = intent.getStringExtra("previousActivity")
        val spQuizRecord = intent.getParcelableExtra<QuizRecord>("Key_quizRecord")
        this.previousActivity = previousActivity!!
        if (spQuizRecord != null) {
            this.quizRecord = spQuizRecord
        }

        if(previousActivity==activitySingleQuiz){
            val spRecordList = intent.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
            val quizIndex = intent.getIntExtra("quiz_index", 0)
            this.quizIndex = quizIndex
            if (spRecordList != null) {
                this.recordList = spRecordList
            }
            initQuestion()
        }else if(previousActivity==activityMain){ //從首頁加入多人考試再回傳的
            val spRecordList = intent.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
            if (spRecordList != null) {
                this.recordList = spRecordList
            }
            initQuestion()
        }
        else if(previousActivity==activityRecordPage){
            ConstantsQuizRecord.getSingleQuizRecord(this, quizRecord._id, onSuccess = { quizRecord, questionRecords->
                this.recordList = questionRecords
                initQuestion()
            }, onFailure = {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            })
        }


    }
    private fun initQuestion(){
        singleRecordBinding.progressBar.max = recordList.size
        for ((index, record) in recordList.withIndex()){
            val tmpQImg =  ArrayList<Bitmap>()
            val tmpAImg =  ArrayList<Bitmap>()

            questionList.add(record.question)
            if(previousActivity==activitySingleQuiz){
                for (item in SingleQuiz.Companion.quizQuestionImages[index]) {
                    val imageBytes: ByteArray = Base64.decode(item, Base64.DEFAULT)
                    val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    tmpQImg.add(decodeImage)
                }
                for (item in SingleQuiz.Companion.quizAnswerImages[index]) {
                    val imageBytes: ByteArray = Base64.decode(item, Base64.DEFAULT)
                    val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    tmpAImg.add(decodeImage)
                }
            }else if(previousActivity==activityMain){
                for (item in MPStartQuiz.Companion.questionImageArr[index]) {
                    tmpQImg.add(item)
                }
                for (item in MPStartQuiz.Companion.answerImageArr[index]) {
                    tmpAImg.add(item)
                }
            }
            else if(previousActivity==activityRecordPage){
                record.question.questionImage?.forEach{
                    val imageBytes: ByteArray = Base64.decode(it, Base64.DEFAULT)
                    val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    tmpQImg.add(decodeImage)
                }
                record.question.answerImage?.forEach{
                    val imageBytes: ByteArray = Base64.decode(it, Base64.DEFAULT)
                    val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    tmpAImg.add(decodeImage)
                }
            }
            questionImageArr.add(tmpQImg)
            answerImageArr.add(tmpAImg)
        }
        setQuestion()
    }
    private fun showAnswer(){
        val currentQuestion = questionList[currentAtQuestion]
        val answerDialog = BottomSheetDialog(this)
        answerDialog.setContentView(R.layout.dialog_question_show_answer)
        val answerDescription: EditText? = answerDialog.findViewById(R.id.answer_description)
        val answerImageList: RecyclerView? = answerDialog.findViewById(R.id.image_list)
        val imageNum: TextView? = answerDialog.findViewById(R.id.image_number)
        val imgLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dialogImageAdapter = ImageRVAdapter(this, answerImageArr[currentAtQuestion])

        answerDescription?.setText(currentQuestion.answerDescription)
        answerImageList?.adapter = dialogImageAdapter
        answerImageList?.layoutManager = imgLayoutManager
        answerImageList?.setHasFixedSize(true)
        if(answerImageArr[currentAtQuestion].size>0){
            imageNum?.text = "1 / ${answerImageArr[currentAtQuestion].size}"
            answerImageList?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val imagePosition = imgLayoutManager.findFirstVisibleItemPosition()
                    imageNum?.text = "${imagePosition + 1} / ${answerImageArr[currentAtQuestion].size}"
                }
            })
        }else{
            imageNum?.text = "此題沒有答案圖片"
        }
        answerDialog.show()
    }

    private fun setQuestionImage(){
        if(questionImageArr[currentAtQuestion].isNotEmpty()){
            imageAdapter = ImageVPAdapter(this, questionImageArr[currentAtQuestion])
            singleRecordBinding.imageViewPager.adapter = imageAdapter
            singleRecordBinding.imageContainer.visibility = View.VISIBLE
            singleRecordBinding.imageNumber.text = "1 / ${questionImageArr[currentAtQuestion].size}"
            singleRecordBinding.imageViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        singleRecordBinding.imageNumber.text = "${position + 1} / ${questionImageArr[currentAtQuestion].size}"
                    }
                }
            )
        }else{
            singleRecordBinding.imageContainer.visibility = View.GONE
        }
    }

    private fun setQuestion(){
        val currentQuestion = questionList[currentAtQuestion]
        singleRecordBinding.QuestionType.text
        singleRecordBinding.QuestionType.text = if(currentQuestion.questionType=="MultipleChoiceS") this.getString(R.string.MultipleChoiceS_CN)
                                            else if(currentQuestion.questionType=="MultipleChoiceM") this.getString(R.string.MultipleChoiceM_CN)
                                            else if(currentQuestion.questionType=="TrueOrFalse") this.getString(R.string.TrueOrFalse_CN)
                                            else if(currentQuestion.questionType=="ShortAnswer") this.getString(R.string.ShortAnswer_CN)
                                            else this.getString(R.string.Filling_CN)
        singleRecordBinding.questionDescription.text = currentQuestion.description
        singleRecordBinding.progressBar.progress = currentAtQuestion+1
        singleRecordBinding.tvProgress.text = (currentAtQuestion+1).toString() + "/" + questionList.size.toString()
        setQuestionImage()



        when(currentQuestion.questionType){
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
        val answerOptions : ArrayList<Int> = ArrayList()
        val selectOption: ArrayList<Int> = ArrayList()
        val btnMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
        val btnMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()
        val btnMarginH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23f, resources.displayMetrics).toInt()
        val btnParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        btnParam.setMargins(btnMarginH, btnMarginTop, btnMarginH, btnMarginBottom)
        btnParam.addRule(RelativeLayout.BELOW, singleRecordBinding.QuestionOption.id)
        singleRecordBinding.gotoBtnContainer.layoutParams = btnParam

        for(index in currentQuestion.options?.indices!!){
            if(currentQuestion.answerOptions?.contains(currentQuestion.options!![index]) == true){
                answerOptions.add(index)
            }
            if(recordList[currentAtQuestion].userAnswerOptions?.contains(currentQuestion.options!![index]) == true){
                selectOption.add(index)
            }

            val tmpOption = Option(Constants.optionNum[index], currentQuestion.options!![index])
            optionlist.add(tmpOption)
        }
        var userAns = "你的答案: "
        for(item in selectOption){
            userAns = userAns + Constants.optionNum[item] + " "
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
        val btnMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
        val btnMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()
        val btnMarginH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23f, resources.displayMetrics).toInt()
        val btnParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        btnParam.setMargins(btnMarginH, btnMarginTop, btnMarginH, btnMarginBottom)
        if(this.shortAnswerView!=null){
            this.shortAnswerView!!.visibility = View.VISIBLE
        }else{
            val textView = TextView(this)
            val answerMinH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150f, resources.displayMetrics).toInt()
            val descriptionMinH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources.displayMetrics).toInt()
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
            val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics)
            val marginH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            val marginV = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
            val layoutParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)

            textView.id = View.generateViewId()
            btnParam.setMargins(btnMarginH, btnMarginTop, btnMarginH, btnMarginBottom)
            layoutParam.setMargins(marginH, marginV, marginH, marginV)
            layoutParam.addRule(RelativeLayout.BELOW, singleRecordBinding.questionDescriptionContainer.id)

            if(isCorrect)
                textView.setBackgroundResource(R.drawable.textview_answer_border)
            else
                textView.setBackgroundResource(R.drawable.textview_wrong_border)
            textView.setPadding(padding)
            textView.layoutParams = layoutParam
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.textSize = textSize
            textView.minHeight = answerMinH
            textView.text = recordList[currentAtQuestion].userAnswerDescription
            textView.hint = "你的答案"
            singleRecordBinding.lowerContainer.addView(textView, 1)
            val answerDescriptionView = singleRecordBinding.root.findViewById<TextView>(textView.id)
            this.shortAnswerView = answerDescriptionView
        }
        btnParam.addRule(RelativeLayout.BELOW, shortAnswerView!!.id)
        singleRecordBinding.gotoBtnContainer.layoutParams = btnParam

    }

    private fun setTrueOrFalse(isCorrect: Boolean){
        val btnMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
        val btnMarginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()
        val btnMarginH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23f, resources.displayMetrics).toInt()
        val btnParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        btnParam.setMargins(btnMarginH, btnMarginTop, btnMarginH, btnMarginBottom)

        if(trueOrFalseView!=null){
            this.trueOrFalseView!!.visibility = View.VISIBLE
        }else{
            val v:View =  layoutInflater.inflate(R.layout.item_option_trueorfalse, singleRecordBinding.recordContainer, false)
            val textViewTrue : TextView = v.findViewById(R.id.option_true)
            val textViewFalse: TextView = v.findViewById(R.id.option_false)
            val vHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics).toInt()
            val tfParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, vHeight)
            tfParams.addRule(RelativeLayout.BELOW, singleRecordBinding.questionDescriptionContainer.id)

            v.layoutParams = tfParams
            v.id = View.generateViewId()

            if(isCorrect) {
                if(questionList[currentAtQuestion].answerOptions?.get(0)  == "true"){
                    textViewTrue.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewTrue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewTrue.compoundDrawablePadding = 0
                }else{
                    textViewFalse.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct))
                    textViewFalse.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit, 0, 0, 0)
                    textViewFalse.compoundDrawablePadding = 0
                }
            }else {
                if(questionList[currentAtQuestion].answerOptions?.get(0) =="true"){
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
            singleRecordBinding.lowerContainer.addView(v, 1)
            this.trueOrFalseView = v
        }
        btnParam.addRule(RelativeLayout.BELOW, this.trueOrFalseView!!.id)
        singleRecordBinding.gotoBtnContainer.layoutParams = btnParam
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
    private fun backBtn(){
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
