package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.ImageVPAdapter
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.databinding.ActivityMpStartQuizBinding
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class  MPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: ActivityMpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private var quizIndex: Int = 0
    private lateinit var optionAdapter: OptionAdapter
    private lateinit var casualDuringTime: ArrayList<Int>
    private lateinit var quizMembers: ArrayList<String>
    private lateinit var questionList : ArrayList<Question>
    private lateinit var userAnsOptions : ArrayList<ArrayList<String>>
    private lateinit var lobbyDialog: AlertDialog
    private var hasStart = false
    private var hasJoin = false
    private var isNext = false //是否所有人都傳送答案到後端了
    private var currentQuestionTimeOut = false
    private var invitingMembers = ArrayList<String>() //在主辦人按下考試前就邀請的成員
    private var isCreator = false
    private lateinit var currentQuiz: Quiz
    private var questionImageArr = ArrayList< ArrayList<Bitmap> >()
    private lateinit var answerImageArr: ArrayList< ArrayList<Bitmap> >
    private var currentAtQuestion: Int = 0
    private var currentSelection = ArrayList<Int>() //被選過的option
    private var selectedView = ArrayList<View>()  //被選過的option的background index和currentSelection 相同
    private var trueOrFalseView: View? = null
    private var trueOrFalseSelected = false
    private var currentAnswer : String = ""
    private var currentQuestionScore = 0
    private var singleQuestionScore = 0
    private lateinit var countDownTimer: CountDownTimer
    private val roomNumber:Int = (100000 .. 999999).random()
    private lateinit var player : MediaPlayer
    private lateinit var imageAdapter: ImageVPAdapter
    private lateinit var socket: Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = ActivityMpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        init()





        //查看考試中成員的分數
        startQuizBinding.checkScore.setOnClickListener {
            returnQuizState()
        }

        //退出考試
        startQuizBinding.exitQuiz.setOnClickListener {
            exitQuiz()
        }

    }
    private fun init(){
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_quizTitle")
        if(title!=null){ //若不是考試主辦人則只會傳id進來
            val questions = intent.getParcelableArrayListExtra<Question>("Key_questions")
            val quizIndex = intent.getIntExtra("quiz_index", 0)
            val startDateTime = intent.getStringExtra("Key_startDateTime")
            val endDateTime = intent.getStringExtra("Key_endDateTime")
            val members = intent.getStringArrayListExtra("Key_members")
            val casualDuringTime = intent.getIntegerArrayListExtra("Key_casualDuringTime")
            quizId = id!!
            quizTitle = title?: ""
            questionList = questions?: ArrayList()
            quizStartDateTime = startDateTime?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            quizEndDateTime = endDateTime?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            invitingMembers = members?: ArrayList()
            this.casualDuringTime = casualDuringTime?: ArrayList()
            this.quizIndex = quizIndex
            isCreator = true
            for(qIndex in 0 until questionList.size){
                val imageArr1d = ArrayList<Bitmap>()
                if(SingleQuiz.Companion.quizQuestionImages.size>qIndex){
                    for(img in SingleQuiz.Companion.quizQuestionImages[qIndex]){
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageArr1d.add(decodeImage)
                    }
                }
                questionImageArr.add(imageArr1d)
            }
            singleQuestionScore = (100/questionList.size)
            startQuizBinding.progressBar.progress = 1
            startQuizBinding.progressBar.max = questionList.size
            startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionList.size.toString()
            userAnsOptions =  ArrayList<ArrayList<String>>(questionList.size)
            loadingLobby()
            connectSocket()
        }else{
            ConstantsQuiz.getSingleQuiz(this, id!!, onSuccess = {quiz ->
                currentQuiz = quiz
                quizId = quiz._id.toString()
                quizTitle = quiz.title.toString()
                questionList = quiz.questions!!
                quizStartDateTime = quiz.startDateTime.toString()
                quizEndDateTime = quiz.endDateTime.toString()
                invitingMembers = quiz.members!!
                this.casualDuringTime = quiz.casualDuringTime!!
                for(question in questionList){
                    val imageArr1d = ArrayList<Bitmap>()
                    for(img in question.questionImage!!) {
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageArr1d.add(decodeImage)
                    }
                    questionImageArr.add(imageArr1d)
                }
                singleQuestionScore = (100/questionList.size)
                startQuizBinding.progressBar.progress = 1
                startQuizBinding.progressBar.max = questionList.size
                startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionList.size.toString()
                userAnsOptions =  ArrayList<ArrayList<String>>(questionList.size)
                loadingLobby()
                connectSocket()
            }, onFailure = {
                Toast.makeText(this, "加入考試失敗! 請重新加入", Toast.LENGTH_LONG).show()
            })
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun setQuestion(){
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

        if(questionImageArr[currentAtQuestion].isNotEmpty()){
            imageAdapter = ImageVPAdapter(this, questionImageArr[currentAtQuestion])
            startQuizBinding.imageViewPager.adapter = imageAdapter
            startQuizBinding.imageContainer.visibility = View.VISIBLE
            startQuizBinding.imageNumber.text = "1 / ${questionImageArr[currentAtQuestion].size}"
            startQuizBinding.imageViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        startQuizBinding.imageNumber.text = "${position + 1} / ${questionImageArr[currentAtQuestion].size}"
                    }
                }
            )
        }else{
            startQuizBinding.imageContainer.visibility = View.GONE
        }


        when(currentQuestion.questionType){
            "MultipleChoiceS",  "MultipleChoiceM"->{
                if(this.trueOrFalseView!=null)
                    trueOrFalseView!!.visibility = View.GONE

                currentQuestion.options?.shuffle()
                for(index in currentQuestion.options?.indices!!){
                    val tmpOption = Option(Constants.optionNum[index], currentQuestion.options!![index])
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
                    currentQuestionTimeOut = false
                }else{
                    currentQuestionTimeOut = true
                }
                selectedView.clear()
                userAnsOptions.add(addUserAns)
            }
            "TrueOrFalse" -> {
                if(trueOrFalseSelected) {
                    addUserAns.add(currentAnswer)
                    currentQuestionTimeOut = false
                }else{
                    addUserAns.add("")
                    currentQuestionTimeOut = true
                }
                userAnsOptions.add(addUserAns)
            }
        }
        val isCorrect = userAnsOptions[currentAtQuestion].toSet() == questionList[currentAtQuestion].answerOptions!!.toSet()
        currentQuestionScore = if(isCorrect) singleQuestionScore else 0
        if(currentQuestionTimeOut) { timeOut() }
        else { finishQuestion() }
    }

    private fun quizEnd(){
        val intent = Intent()
        finishQuiz()
        player.stop()
        for(i in currentAtQuestion until questionList.size){
            val tmpAnsOptions = ArrayList<String>()
            userAnsOptions.add(tmpAnsOptions)
        }
        intent.setClass(this, MPQuizFinish::class.java)
        intent.putExtra("Key_userAnsOptions", userAnsOptions)
        intent.putExtra("Key_id", quizId)
        intent.putExtra("Key_title", quizTitle)
//        intent.putExtra("Key_startDate", startDateStr)
//        intent.putExtra("Key_endDate", endDateStr)
        intent.putExtra("Key_type", Constants.quizTypeCasual)
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
            tfParams.addRule(RelativeLayout.BELOW, startQuizBinding.questionDescriptionContainer.id)
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
    private fun loadingLobby(){
        val builder = AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle)
        val v:View =  layoutInflater.inflate(R.layout.dialog_mp_quiz_start_lobby, null)
        builder.setView(v)
        val dialog = builder.create()
        lobbyDialog = dialog
        dialog.show()
        val quizStart:TextView = v.findViewById(R.id.Quiz_start)
        val roomNumberTextView:TextView  = v.findViewById(R.id.room_number)
        val copyQuizID: Button = v.findViewById(R.id.copy_quizID)
        val quizMembers: TextView = v.findViewById(R.id.Quiz_members)

        copyQuizID.setOnClickListener {
            val cm: ClipboardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val myClip = ClipData.newPlainText("邀請碼", quizId)
            cm.setPrimaryClip(myClip)
            Toast.makeText(this, "成功複製邀請碼", Toast.LENGTH_SHORT).show()
        }
        quizMembers.text = Constants.userId + "(你)"
        roomNumberTextView.text = quizId

        if(isCreator){
            quizStart.setOnClickListener {
                startQuiz()
            }
        }else{
            quizStart.text = "等待考試主辦人開始考試"
            quizStart.isEnabled = false
        }
    }
    private fun startMusic(){
        player = MediaPlayer.create(this, R.raw.start_quiz_music)
        player.setOnCompletionListener {
            try {
                player.stop()
                player.prepare()
                player.start()
            }catch (e: Exception){
                Log.d("player error", e.toString())
            }
        }
        player.setVolume(6.0f, 6.0f)
        player.start()

    }
    private fun exitQuiz(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("確定退出考試?")
        builder.setPositiveButton("確定") { dialog, which ->
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
//            player.stop()
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

    private fun connectSocket(){
        val connectURL = Constants.BASE_URL + "funnyQuiz"
        try{
            socket = IO.socket("https://quizbank.soselab.tw/funnyQuiz")
            socket.connect()
            socket.on(Socket.EVENT_CONNECT, connectReturn)
        } catch (e: URISyntaxException) {
            Log.d("error in connecting occur\n\n\n\n\n", e.toString())
            e.printStackTrace()
        }
    }
    private fun joinQuiz(){
        Log.d("now joining", "")
        socket.emit("join_quiz", quizId, Constants.userId)
        socket.on("joinQuiz", joinReturn)
    }
    private fun startQuiz(){
        Log.d("now starting quiz", "")
        socket.emit("start_quiz", quizId, questionList[0]._id, questionList.size)
        socket.on("startQuiz", startReturn)
    }
    private fun finishQuestion(){
        socket.emit("finish_question", quizId, Constants.userId, userAnsOptions[currentAtQuestion], currentQuestionScore,
            questionList[currentAtQuestion+1]._id)
    }
    private fun timeOut(){
        socket.emit("timeout", quizId, Constants.userId, questionList[currentAtQuestion+1]._id)
    }
    private fun finishQuiz(){
        socket.emit("finish_quiz", quizId)
    }
    private fun returnQuizState(){
        socket.emit("return_quiz_state", quizId)
    }
    private val connectReturn = Emitter.Listener {args->
        Log.d("connect successful",args.toString())
//        if(!hasJoin){
            joinQuiz()
//        }
        hasJoin = true
    }
    private val joinReturn = Emitter.Listener { args->
        val data0 = args[0]
        socket.on("startQuiz", startReturn)
        Log.d("join successful", "user count is $data0")
        Toast.makeText(this, "join successful ", Toast.LENGTH_SHORT).show()
    }
    private val startReturn = Emitter.Listener { args->
        Log.d("start successful",args.toString())
        if(hasStart) {
            socket.on("finishQuestion", finishQuestionReturn)
            socket.on("timeout", timeOutReturn)
            socket.on("finishQuiz", finishQuizReturn)
            socket.on("returnQuizState", quizStateReturn)
            runOnUiThread {
                Log.d("start successful 1", args.toString())
//                startMusic()
                setQuestion()
                lobbyDialog.dismiss()
            }
        }
        hasStart = true
    }
    private val finishQuestionReturn = Emitter.Listener { args->
        Log.d("finish Question successful",args.toString())
        for(i in args.indices){
            Log.d("return finish question $i is", args[i].toString())
            isNext = true
        }
    }
    private val timeOutReturn = Emitter.Listener { args->
        Log.d("time out successful",args.toString())
        for(i in args.indices){
            Log.d("return time out $i is", args[i].toString())
        }
    }
    private val finishQuizReturn = Emitter.Listener { args->
        Log.d("finish Quiz successful", args.toString())
    }
    private val quizStateReturn = Emitter.Listener { args->
        Log.d("quiz state return successful",args.toString())
        for(i in args.indices){
            Log.d("return quiz state $i is", args[i].toString())
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(::socket.isInitialized) {
            socket.disconnect()
            socket.off(Socket.EVENT_CONNECT)
            socket.off("joinQuiz")
            socket.off("startQuiz")
            socket.off("finishQuestion")
            socket.off("timeout")
            socket.off("finishQuiz")
            socket.off("returnQuizState")
        }
    }
}