package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.LinearLayoutWrapper
import com.example.quizbanktest.adapters.quiz.QuestionAdapter
import com.example.quizbanktest.databinding.ActivitySingleQuizBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.models.QuizRecord
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz

class SingleQuiz: AppCompatActivity() {
    private lateinit var quizBinding: ActivitySingleQuizBinding
    private lateinit var questionlist : ArrayList<Question>
    private lateinit var casualDuringTime : ArrayList<Int>
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizType: String
    private lateinit var quizStatus: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var quizAdapter: QuestionAdapter
    private var differentFromQuizList = false
    private var hadPutQuiz = false
    private var isModified = false
    private var duringTime: Int = -1
    private var quizIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivitySingleQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)
        init()
        quizBinding.QuestionList.layoutManager = LinearLayoutWrapper(this)
        quizBinding.QuestionList.setHasFixedSize(true)
        quizAdapter = QuestionAdapter(this, questionlist, casualDuringTime)
        quizAdapter.setQuizIndex(quizIndex)
        quizAdapter.setQuizType(quizType)
        quizBinding.QuestionList.adapter = quizAdapter
        quizBinding.backBtn.setOnClickListener {
            backBtn()
        }
        quizBinding.saveBtn.setOnClickListener {
            saveQuiz()
        }
        quizBinding.quizSetting.setOnClickListener { quizSetting() }
        quizBinding.startQuiz.setOnClickListener{
            startQuiz(questionlist, quizId)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in ", "single Quiz")
        Log.d("request code=", requestCode.toString())

        if(requestCode < 1000)  //從singleQuestion傳回single quiz的內容
        {
            resultFromQuestion(requestCode, resultCode, data)
        }
        else if(requestCode == 1000)    //從quiz setting傳回
        {
            if(resultCode == Constants.RESULT_DELETE) {
                isModified = true
                deleteQuiz()
                val intentBack = Intent()
                intentBack.putExtra("Key_type", quizType)
                setResult(Constants.RESULT_DELETE, intentBack)
                finish()
            }
            else if(resultCode == RESULT_OK)
            {
                if (data != null) {
                    val tmpTitle = data.getStringExtra("Key_title")
                    val tmpQuizStartDateTime = data.getStringExtra("Key_startDateTime")
                    val tmpQuizEndDateTime = data.getStringExtra("Key_endDateTime")
                    val tmpQuizMembers = data.getStringArrayListExtra("Key_members")
                    val tmpDuringTime = data.getIntExtra("Key_duringTime", 0)
                    if(tmpTitle!=quizTitle || tmpQuizStartDateTime!=quizStartDateTime || tmpQuizEndDateTime!=quizEndDateTime ||
                        tmpQuizMembers!=quizMembers || tmpDuringTime!=duringTime){
                        isModified = true
                    }

                    if (tmpTitle != null)
                        quizTitle = tmpTitle
                    if (tmpQuizStartDateTime != null)
                        quizStartDateTime = tmpQuizStartDateTime
                    if (tmpQuizEndDateTime != null)
                        quizEndDateTime = tmpQuizEndDateTime
                    if (tmpQuizMembers != null) {
                        quizMembers = tmpQuizMembers
                    }
                    duringTime = tmpDuringTime

                    quizBinding.quizTitle.text = title
                }
            }

        }
        else if(requestCode == 2000) //考完試後回傳
        {
            if(resultCode == RESULT_OK) { //保存考試紀錄
                val intent = Intent()
                intent.setClass(this, SPSingleRecord::class.java)
                val questionRecordList = data?.getParcelableArrayListExtra<QuestionRecord>("Key_questionRecord")
                val quizRecord = data?.getParcelableExtra<QuizRecord>("Key_quizRecord")

                intent.putParcelableArrayListExtra("Key_questionRecord", questionRecordList)
                intent.putExtra("Key_quizRecord", quizRecord)
                intent.putExtra("quiz_index", this.quizIndex)
                startActivity(intent)
            }
            else if(resultCode == RESULT_CANCELED){ //不保存考試紀錄
                finish()
            }
        }
    }

    private fun startQuiz(questionList: ArrayList<Question>, quizId: String){
        determineStatus()
        if(quizStatus!="draft"){
            val intent = Intent()
            intent.putExtra("Key_id", quizId)
            intent.putExtra("Key_quizTitle", quizTitle)
            intent.putExtra("Key_type", quizType)
            intent.putExtra("quiz_index", quizIndex)
            intent.putParcelableArrayListExtra("Key_questions", questionList)
            if(quizType==Constants.quizTypeCasual) {
                intent.setClass(this, MPStartQuiz::class.java)
                intent.putIntegerArrayListExtra("Key_casualDuringTime", casualDuringTime)
                intent.putStringArrayListExtra("Key_members", quizMembers)
                intent.putExtra("Key_startDateTime", quizStartDateTime)
                intent.putExtra("Key_endDateTime", quizEndDateTime)
            }else if(quizType==Constants.quizTypeSingle){
                intent.setClass(this, SPStartQuiz::class.java)
                intent.putExtra("Key_duringTime", duringTime)
            }

            if(isModified){  //有改過 儲存後再考。 之所以寫成這麼複雜 是因為害怕異步執行
                val putQuiz = Quiz(quizId, quizTitle, quizType, quizStatus, duringTime, casualDuringTime, quizStartDateTime, quizEndDateTime, quizMembers, questionlist)

                ConstantsQuiz.putQuiz(this, putQuiz, onSuccess = {
                    isModified = false

                    startActivityForResult(intent, 2000)
                }, onFailure = {
                    if(quizType==Constants.quizTypeCasual){ //多人考試使用資料庫的資料來考試 不使用目前user的本地端資料
                        Toast.makeText(this, "考試儲存錯誤 請重新儲存", Toast.LENGTH_SHORT).show()
                    }else if(quizType==Constants.quizTypeSingle){ //單人考試就算了 使用修改後的版本考
                        startActivityForResult(intent, 2000)
                    }
                })
            } else{
                startActivityForResult(intent, 2000)
            }
        }
        else{
            AlertDialog.Builder(this).setTitle("考試尚未設定完成!").setPositiveButton("我懂", null).show()
        }
    }
    private fun saveQuiz(){
        determineStatus()
        if(isModified){
            differentFromQuizList = true
        }
        if(quizType==Constants.quizTypeCasual){
            var duringTime = 0
            for(time in casualDuringTime){
                duringTime+=time
            }
            this.duringTime = duringTime
        }

        val putQuiz = Quiz(quizId, quizTitle, quizType, quizStatus, duringTime, casualDuringTime, quizStartDateTime, quizEndDateTime, quizMembers, questionlist)
        ConstantsQuiz.putQuiz(this, putQuiz, onSuccess = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            isModified = false
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
    private fun backAndUpdateQuizInQuizList(){ //返回並更新quizList中的此Quiz
        val intentBack = Intent()
        intentBack.putExtra("Key_title", quizTitle)
        intentBack.putExtra("Key_type", quizType)
        intentBack.putExtra("Key_status", quizStatus)
        intentBack.putExtra("Key_startDateTime", quizStartDateTime)
        intentBack.putExtra("Key_endDateTime", quizEndDateTime)
        intentBack.putParcelableArrayListExtra("Key_questions", questionlist)
        if(quizType=="casual"){
            intentBack.putExtra("Key_casualDuringTime", casualDuringTime)
            intentBack.putStringArrayListExtra("Key_members", quizMembers)
        }else{
            intentBack.putExtra("Key_duringTime", duringTime)
        }
        setResult(RESULT_OK, intentBack)
        finish()
    }
    private fun deleteQuiz(){
        ConstantsQuiz.deleteQuiz(this, quizId, onSuccess = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }, onFailure = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun determineStatus(){
        var status = Constants.quizStatusReady
        if(questionlist.isEmpty() || duringTime==0){
            status = Constants.quizStatusDraft
        }
        else{
            for(question in questionlist){
                if(question.questionType==Constants.questionTypeShortAnswer)
                {
                    if(quizType==Constants.quizTypeCasual){
                        Toast.makeText(this, "多人考試不能有簡答題!", Toast.LENGTH_LONG).show()
                        status = Constants.quizStatusDraft
                        break
                    }else if(quizType==Constants.quizTypeSingle){
                        if(question.description.isNullOrEmpty()){
                            status = Constants.quizStatusDraft
                            break
                        }
                    }
                }
                else {
                    if(question.answerOptions.isNullOrEmpty() || question.options.isNullOrEmpty() || question.description.isNullOrEmpty()){
                        status = Constants.quizStatusDraft
                        break
                    }
                }

            }
        }
        quizStatus = status
    }

    private fun init()
    {
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val status = intent.getStringExtra("Key_status")
        val startDateTime = intent.getStringExtra("Key_startDateTime")
        val endDateTime = intent.getStringExtra("Key_endDateTime")
        val quizIndex = intent.getIntExtra("quiz_index", 0)
//      **需要api 33以上**
//      val questions = intent.getParcelableArrayListExtra("Key_questions", Question::class.java)
        val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
        val members = intent.getStringArrayListExtra("Key_members")
        val tmpCasualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")
        val duringTime = intent.getIntExtra("Key_duringTime", 0)

        if (questions != null)
            questionlist = questions
        if (members != null)
            quizMembers = members
        if (title != null)
            quizTitle = title
        if (id != null)
            quizId = id
        if (type != null)
            quizType = type
        if (status != null)
            quizStatus = status
        if (startDateTime != null)
            quizStartDateTime = startDateTime
        if (endDateTime != null)
            quizEndDateTime = endDateTime
        if(tmpCasualDuringTime!= null) {
            this.casualDuringTime = tmpCasualDuringTime
        }else{
            this.casualDuringTime = ArrayList()
        }
        if(quizType=="casual" && casualDuringTime.size<questionlist.size){
            for (i in casualDuringTime.size until questionlist.size){
                casualDuringTime.add(20)
            }
        }
        isModified = false
        differentFromQuizList = false
        this.quizIndex = quizIndex
        this.duringTime = duringTime
        quizBinding.quizTitle.text = title
    }
    private fun quizSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuiz, SingleQuizSetting::class.java)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_status", quizStatus)
        intent.putExtra("Key_startDateTime", quizStartDateTime)
        intent.putExtra("Key_endDateTime", quizEndDateTime)
        intent.putExtra("Key_type", quizType)
        if(quizTitle=="casual") {
            intent.putStringArrayListExtra("Key_members", quizMembers)
        }
        else{
            intent.putExtra("Key_duringTime", duringTime)
        }
        startActivityForResult(intent, 1000)
    }

    private fun resultFromQuestion(requestCode: Int, resultCode: Int, data: Intent?){
        val tmpQuestion = questionlist[requestCode]
        if(resultCode== RESULT_OK) {
            if (data != null) {
                val tmpTag = ArrayList<String>()
                val tmpTitle = data.getStringExtra("Key_title")
                val tmpDescription = data.getStringExtra("Key_description")
                val tmpAnswerOptions = data.getStringArrayListExtra("Key_answerOptions")
                val tmpAnswerDescription = data.getStringExtra("Key_answerDescription")
                val tmpNumber = data.getStringExtra("Key_number")
                val tmpQuestionType = data.getStringExtra("Key_type")
                val tmpOptions = data.getStringArrayListExtra("Key_options")
                for (i in 0 until data.getIntExtra("Key_tagNum", 0)) {
                    tmpTag.add(data.getStringExtra("Key_tag$i")!!)
                }

                if(tmpTag!=tmpQuestion.tag || tmpTitle!=tmpQuestion.title || tmpDescription!=tmpQuestion.description ||
                    tmpAnswerOptions!=tmpQuestion.answerOptions || tmpAnswerDescription!=tmpQuestion.answerDescription ||
                    tmpNumber!=tmpQuestion.number || tmpQuestionType!=tmpQuestion.questionType ||
                    tmpOptions!=tmpQuestion.options){
                    isModified = true
                }
                tmpQuestion.tag = tmpTag
                tmpQuestion.options = tmpOptions
                tmpQuestion.title = tmpTitle
                tmpQuestion.description = tmpDescription
                tmpQuestion.answerOptions = tmpAnswerOptions
                tmpQuestion.answerDescription = tmpAnswerDescription
                tmpQuestion.number = tmpNumber
                tmpQuestion.questionType = tmpQuestionType
                for(option in tmpQuestion.options!!){
                    Log.d("option is ", option)
                }
                for(answerOption in tmpQuestion.answerOptions!!){
                    Log.d("answer option is ", answerOption)
                }
                if (quizType == "casual") {
                    val tmpTimeLimit = data.getIntExtra("Key_timeLimit", 0)
                    if(tmpTimeLimit != casualDuringTime[requestCode]){
                        isModified = true
                    }
                    casualDuringTime[requestCode] = tmpTimeLimit
                    quizAdapter.updateTimeLimit(casualDuringTime[requestCode], requestCode)
                }

                questionlist[requestCode] = tmpQuestion
//                quizBinding.QuestionList.adapter?.notifyItemChanged(requestCode)
                quizBinding.QuestionList.adapter?.notifyDataSetChanged()
            }
        }else if(resultCode== Constants.RESULT_DELETE){
            isModified = true
            questionlist.removeAt(requestCode)
            quizBinding.QuestionList.adapter?.notifyDataSetChanged()
//            quizAdapter.notifyItemChanged(requestCode)
//            for(index in requestCode until questionlist.size){
//                quizAdapter.notifyItemChanged(index)
//            }
        }
    }
    private fun backBtn(){
        if(differentFromQuizList && !isModified){ //使用者按下儲存考試 再按返回
            backAndUpdateQuizInQuizList()
        }
        else if(!isModified){ //使用者沒改過東西
            setResult(RESULT_CANCELED)
            finish()
        }else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("是否保存修改紀錄?")
            builder.setPositiveButton("確定") { dialog, which ->
                saveQuiz()
                backAndUpdateQuizInQuizList()
            }
            builder.setNegativeButton("取消"){ dialog, which ->
                setResult(RESULT_CANCELED)
                finish()
            }
            builder.show()
        }
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
