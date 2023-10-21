package com.example.quizbanktest.activity.quiz
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.*
import com.example.quizbanktest.databinding.ActivityMpStartQuizBinding
import com.example.quizbanktest.models.*
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class  MPStartQuiz: AppCompatActivity() {
    private lateinit var startQuizBinding: ActivityMpStartQuizBinding
    private lateinit var quizId: String
    private lateinit var quizTitle: String
    private lateinit var quizStartDateTime: String
    private lateinit var quizEndDateTime: String
    private var quizIndex: Int = 0
    private lateinit var optionAdapter: OptionAdapter
    private lateinit var memberStateAdapter: QuizMembersStateAdapter
    private lateinit var memberInLobbyAdapter: QuizMemberInLobbyAdapter
    private lateinit var casualDuringTime: ArrayList<Int>
    private var quizMembers = ArrayList<QuizMember>()
    private lateinit var questionList : ArrayList<Question>
    private lateinit var userAnsOptions : ArrayList<ArrayList<String>>
    private lateinit var lobbyDialog: AlertDialog
    private var isNext = false //是否所有人都傳送答案到後端了
    private var currentQuestionTimeOut = false
    private var invitingMembers = ArrayList<String>() //在主辦人按下考試前就邀請的成員
    private val memberIDToName: MutableMap<String, String> = mutableMapOf()
    private var isCreator = false
    private var duringTime = 0
    private lateinit var currentQuiz: Quiz
    private var correctPoints = 0
    private var userTotalCorrect = 0
    private var requestState = 1
    private var badNetWorkEnd = 1
    private var requestStart = 0
    private var currentAtQuestion: Int = 0
    private var quizMemberNumber = 0
    private var currentSelection = ArrayList<Int>() //被選過的option
    private var selectedView = ArrayList<View>()  //被選過的option的background index和currentSelection 相同
    private var trueOrFalseView: View? = null
    private var trueOrFalseSelected = false
    private lateinit var textViewTrue: TextView
    private var currentAnswer : String = ""
    private var currentQuestionScore = 0
    private var singleQuestionScore = 0
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var roomNumber:TextView
    private lateinit var player : MediaPlayer
    private lateinit var correctPlayer: MediaPlayer
    private lateinit var wrongPlayer: MediaPlayer
    private lateinit var imageAdapter: ImageVPAdapter
    private lateinit var socket: Socket
    private var onReturnStateListener: OnReturnStateListener? = null
    companion object{
        var questionImageArr = ArrayList< ArrayList<Bitmap> >()
        var answerImageArr = ArrayList< ArrayList<Bitmap> >()
    }
    interface OnReturnStateListener {
        fun onReturn()
    }
    fun setOnReturnStateListener(onReturnListener: OnReturnStateListener) {
        onReturnStateListener = onReturnListener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startQuizBinding = ActivityMpStartQuizBinding.inflate(layoutInflater)
        setContentView(startQuizBinding.root)
        startQuizBinding.startQuizContainer.visibility  = View.GONE

        val opts = io.socket.client.IO.Options()
//        opts.host = "192.168.1.116"
        opts.transports = arrayOf("websocket")
//        opts.transportOptions
//        opts.rememberUpgrade = false
//        socket = IO.socket("https://quizbank.soselab.tw/funnyQuiz", opts)
        val connectURL = Constants.BASE_URL + "funnyQuiz"
        socket = IO.socket(connectURL, opts)

        init()
        player = MediaPlayer.create(this, R.raw.start_quiz_music)
        correctPlayer =  MediaPlayer.create(this, R.raw.correct_sound)
        wrongPlayer = MediaPlayer.create(this, R.raw.wrong_sound)

        //查看考試中成員的分數
        startQuizBinding.checkScore.setOnClickListener {
            setOnReturnStateListener(object : MPStartQuiz.OnReturnStateListener{
                override fun onReturn() {
                    showQuizMemberState()
                }
          })
            returnQuizState()
        }

        //退出考試
        startQuizBinding.exitQuiz.setOnClickListener {
            exitQuiz()
        }

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
    private fun init(){
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_quizTitle")
        questionImageArr.clear()
        answerImageArr.clear()
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
            for(qTime in this.casualDuringTime){
                duringTime += qTime
            }
            for(qIndex in 0 until questionList.size){
                val qimageArr1d = ArrayList<Bitmap>()
                val aimageArr1d = ArrayList<Bitmap>()
                if(SingleQuiz.Companion.quizQuestionImages.size>qIndex){
                    for(img in SingleQuiz.Companion.quizQuestionImages[qIndex]){
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        qimageArr1d.add(decodeImage)
                    }
                }
                if(SingleQuiz.Companion.quizAnswerImages.size>qIndex){
                    for(img in SingleQuiz.Companion.quizAnswerImages[qIndex]){
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        aimageArr1d.add(decodeImage)
                    }
                }
                questionImageArr.add(qimageArr1d)
                answerImageArr.add(aimageArr1d)
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
                for(qTime in this.casualDuringTime){
                    duringTime += qTime
                }
                for(question in questionList){
                    val qimageArr1d = ArrayList<Bitmap>()
                    val aimageArr1d = ArrayList<Bitmap>()
                    for(img in question.questionImage!!) {
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        qimageArr1d.add(decodeImage)
                    }
                    for(img in question.answerImage!!) {
                        val imageBytes: ByteArray = Base64.decode(img, Base64.DEFAULT)
                        val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        aimageArr1d.add(decodeImage)
                    }
                    questionImageArr.add(qimageArr1d)
                    answerImageArr.add(aimageArr1d)
                }
                singleQuestionScore = (100/questionList.size)
                startQuizBinding.progressBar.progress = 1
                startQuizBinding.progressBar.max = questionList.size
                startQuizBinding.tvProgress.text = (currentAtQuestion+1).toString() + ":" + questionList.size.toString()
                userAnsOptions =  ArrayList<ArrayList<String>>(questionList.size)
                loadingLobby()
                connectSocket()
            }, onFailure = {
                Toast.makeText(this, "加入考試失敗! 請重新加入 $it", Toast.LENGTH_LONG).show()
            })
        }

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
                    startQuizBinding.QuestionOption.visibility = View.GONE
                    currentAtQuestion+=1
                    showUserFigure()
                    quizEnd()
                }else{
                    currentAtQuestion+=1
                    showUserFigure()
                    Handler().postDelayed({
                        setQuestion()
                    },1000)
                }
            }
            override fun onTick(millisUntilFinished: Long) {
                val totalRemain = millisUntilFinished/10
                startQuizBinding.progressBar.progress = totalRemain.toInt()
            }
        }.start()
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
            this.textViewTrue = textViewTrue
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
        if(isCorrect){
            currentQuestionScore = 1
            correctPoints += 1
            userTotalCorrect += 1
            startQuizBinding.checkScore.text = correctPoints.toString()
            Log.d("current q is correct", "")
        }else{
            currentQuestionScore = 0
            Log.d("current q is not correct my ans is ${userAnsOptions[currentAtQuestion].toSet()}", "correct is ${questionList[currentAtQuestion].answerOptions!!.toSet()}")
        }

        if(currentQuestionTimeOut) { timeOut() }
        else { finishQuestion() }
    }

    private fun quizEnd(){
        val intent = Intent()
        val endDate = LocalDateTime.now()
        val startDateTimeStr = quizStartDateTime.format(Constants.dateTimeFormat)
        val endDateTimeStr = endDate.format(Constants.dateTimeFormat)

        player.stop()

        for(i in currentAtQuestion until questionList.size){
            val tmpAnsOptions = ArrayList<String>()
            userAnsOptions.add(tmpAnsOptions)
        }
        intent.setClass(this, MPQuizFinish::class.java)
        intent.putExtra("Key_userAnsOptions", userAnsOptions)
        intent.putExtra("Key_id", quizId)
        intent.putExtra("Key_title", quizTitle)
        intent.putExtra("Key_quizMembers", quizMembers)
        intent.putExtra("Key_startDateTime", startDateTimeStr)
        intent.putExtra("Key_endDateTime", endDateTimeStr)
        intent.putExtra("Key_duringTime", duringTime)
        intent.putParcelableArrayListExtra("Key_questions", questionList)
        setOnReturnStateListener(object : MPStartQuiz.OnReturnStateListener{
            override fun onReturn() {
                badNetWorkEnd = 0
                startActivityForResult(intent, 1000)
            }
        })
        Handler().postDelayed({
            returnQuizState()
        }, 700)
        Handler().postDelayed({
            finishQuiz()
        }, 1500)
        Handler().postDelayed({
            if(badNetWorkEnd == 1){
                startActivityForResult(intent, 1000)
                Toast.makeText(this, "網路太差 考試答題記錄取得錯誤", Toast.LENGTH_SHORT).show()
            }
        }, 1500)

    }
    private fun exitQuiz(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("確定退出考試?")
        builder.setPositiveButton("確定") { dialog, which ->
            if (::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            if(this::player.isInitialized){
                player.stop()
            }
            if(this::correctPlayer.isInitialized){
                correctPlayer.stop()
            }
            if(this::wrongPlayer.isInitialized){
                wrongPlayer.stop()
            }
            startQuizBinding.startQuizContainer.visibility  = View.GONE
            lobbyDialog.dismiss()
            finish()
        }
        builder.setNegativeButton("取消", null)
        builder.show()
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
        val exitQuiz: ImageButton = v.findViewById(R.id.exit_quiz)
        val membersRV: RecyclerView = v.findViewById(R.id.members_in_lobby)

        roomNumber = roomNumberTextView
        roomNumber.text = quizId
        memberInLobbyAdapter = QuizMemberInLobbyAdapter(this, this.quizMembers)
        membersRV.layoutManager = LinearLayoutManager(this)
        membersRV.setHasFixedSize(true)
        membersRV.adapter = memberInLobbyAdapter
        exitQuiz.setOnClickListener {
            exitQuiz()
        }
        copyQuizID.setOnClickListener {
            val cm: ClipboardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val myClip = ClipData.newPlainText("邀請碼", quizId)
            cm.setPrimaryClip(myClip)
//            Toast.makeText(this, "成功複製邀請碼", Toast.LENGTH_SHORT).show()
        }

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

        player.setOnCompletionListener {
            try {
                player.stop()
                player.prepare()
                player.start()
            }catch (e: Exception){
                Log.d("player error", e.toString())
            }
        }
        player.setVolume(30.0f, 30.0f)
        player.start()
    }
    private fun correctMusic(){
        correctPlayer.setVolume(30.0f, 30.0f)
        correctPlayer.start()
    }
    private fun wrongMusic(){
        wrongPlayer.setVolume(40.0f, 40.0f)
        wrongPlayer.start()
    }

    private fun showUserFigure(){
        val builder = AlertDialog.Builder(this)
        val actions:View =  layoutInflater.inflate(R.layout.dialog_show_user_figure, startQuizBinding.root,false)
        val userF: ImageView = actions.findViewById(R.id.user_figure)
        val isCorrectStr: TextView = actions.findViewById(R.id.is_correct)
        builder.setView(actions)
        val dialog: AlertDialog = builder.create()
        val dialogWindow = dialog.window
        val dialogParm: WindowManager.LayoutParams? = dialogWindow?.attributes
        dialogWindow?.setGravity(Gravity.TOP)
        dialogWindow?.setDimAmount(0f)
        dialogWindow?.attributes = dialogParm
        if(currentQuestionScore==1){
            isCorrectStr.text = "正確!  技能點+1"
            correctMusic()
        }else{
            isCorrectStr.text = "錯誤!"
            wrongMusic()
        }

        val incorrectNum = currentAtQuestion - userTotalCorrect
        val correctDiff = userTotalCorrect - incorrectNum

        when {
            correctDiff < -2 -> {
                userF.setImageResource(R.drawable.figure_image0)
            }
            correctDiff == -2 -> {
                userF.setImageResource(R.drawable.figure_image1)
            }
            correctDiff == -1 -> {
                userF.setImageResource(R.drawable.figure_image2)
            }
            correctDiff == 0 -> {
                userF.setImageResource(R.drawable.figure_image3)
            }
            correctDiff == 1 -> {
                userF.setImageResource(R.drawable.figure_image4)
            }
            correctDiff == 2 -> {
                userF.setImageResource(R.drawable.figure_image5)
            }
            correctDiff == 3 -> {
                userF.setImageResource(R.drawable.figure_image6)
            }
            correctDiff == 4 -> {
                userF.setImageResource(R.drawable.figure_image7)
            }
            correctDiff > 4 -> {
                userF.setImageResource(R.drawable.figure_image8)
            }
        }
        dialog.show()
        Handler().postDelayed({
            dialog.dismiss()
        }, 1000)
    }
    private fun showQuizMemberState(){
        Log.d("showing members state quizMembers is", quizMembers.toString())
        val memberStateDialog = BottomSheetDialog(this)
        memberStateDialog.setContentView(R.layout.dialog_quiz_members_state)
        val memberRV: RecyclerView? = memberStateDialog.findViewById(R.id.members_state_list)
        memberStateAdapter = QuizMembersStateAdapter(this, quizMembers, currentAtQuestion )
        memberStateAdapter.setOnAttackListener(object :  QuizMembersStateAdapter.OnAttackListener{
            override fun onAttack(receiverID: String, receiverName: String) {
                attackList(receiverID, receiverName)
                memberStateDialog.dismiss()
            }
        })
        memberRV?.adapter = memberStateAdapter
        if(quizMemberNumber<3){
            memberRV?.layoutManager = GridLayoutManager(this, quizMemberNumber)
        }else{
            memberRV?.layoutManager = GridLayoutManager(this, 3)
        }
        memberRV?.setHasFixedSize(true)
        memberStateDialog.show()
    }
    private fun attackList(receiverID: String, receiverName: String){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_quiz_attack_list, null)
        val turnPage: TextView = v.findViewById(R.id.turn_page)
        val throwEgg: TextView = v.findViewById(R.id.throw_egg)
        val stealOption: TextView = v.findViewById(R.id.steal_option)
        builder.setView(v)
        val dialog: AlertDialog = builder.create()

        turnPage.setOnClickListener {
            if(correctPoints>0){
                attackMember(receiverID, 0)
                correctPoints -= 1
                startQuizBinding.checkScore.text = correctPoints.toString()
                dialog.dismiss()
                Toast.makeText(this, "${receiverName.substring(0,3)} 被你翻頁的氣勢嚇到了!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "技能點數不足!", Toast.LENGTH_SHORT).show()
            }
        }
        throwEgg.setOnClickListener {
            if(correctPoints>1){
                attackMember(receiverID, 2)
                correctPoints -= 2
                startQuizBinding.checkScore.text = correctPoints.toString()
                dialog.dismiss()
                Toast.makeText(this, "你砸的${receiverName.substring(0,3)}滿臉蛋液!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "技能點數不足!", Toast.LENGTH_SHORT).show()
            }
        }
        stealOption.setOnClickListener {
            if(correctPoints>2){
                attackMember(receiverID, 1)
                correctPoints -= 3
                startQuizBinding.checkScore.text = correctPoints.toString()
                dialog.dismiss()
                Toast.makeText(this, "你偷走了${receiverName.substring(0,3)}一個選項! 他將無法使用那個選項", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "技能點數不足!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    private fun hookAnim() {
        startQuizBinding.hook.visibility = View.VISIBLE
        startQuizBinding.hook.translationZ = 10f
        val startX = startQuizBinding.hook.translationX  // 獲取當前的 X 座標
        val startY = startQuizBinding.hook.translationY  // 獲取當前的 Y 座標
        val location = IntArray(2)
        startQuizBinding.QuestionOption.getLocationOnScreen(location)

        // 計算目標座標以使view位於parent的中央
        val targetX = location[0].toFloat()
        val targetY = location[1].toFloat()

        val duration: Long = 1000
        val interpolator = AccelerateDecelerateInterpolator()

        val animatorMoveX = ObjectAnimator.ofFloat(startQuizBinding.hook, View.TRANSLATION_X, startX, targetX)
        val animatorMoveY = ObjectAnimator.ofFloat(startQuizBinding.hook, View.TRANSLATION_Y, startY, targetY)
        val animatorReturnX = ObjectAnimator.ofFloat(startQuizBinding.hook, View.TRANSLATION_X, targetX, startX)
        val animatorReturnY = ObjectAnimator.ofFloat(startQuizBinding.hook, View.TRANSLATION_Y, targetY, startY)

        // 設定動畫的持續時間和插值器
        for (animator in arrayOf(animatorMoveX, animatorMoveY, animatorReturnX, animatorReturnY)) {
            animator.duration = duration
            animator.interpolator = interpolator
        }

        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }
            override fun onAnimationEnd(animation: Animator) {
                startQuizBinding.hook.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet.play(animatorMoveX).with(animatorMoveY).before(animatorReturnX).before(animatorReturnY)
        animatorSet.start()
    }
    private fun shakeAnim() {
        val rootView = findViewById<View>(android.R.id.content)

        val shakeAnimatorX = ObjectAnimator.ofFloat(
            rootView, "translationX",
            0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        ).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.RESTART
        }

        val shakeAnimatorY = ObjectAnimator.ofFloat(
            rootView, "translationY",
            0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f
        ).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.RESTART
        }

        val shakeSet = AnimatorSet()
        shakeSet.playTogether(shakeAnimatorX, shakeAnimatorY)
        shakeSet.start()
    }

    private fun throwEggAnim(){
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
        val imageView = ImageView(this)
        val layoutParam = LinearLayout.LayoutParams(width, width)
        layoutParam.gravity = Gravity.END
        imageView.layoutParams = layoutParam
        imageView.setImageResource(R.drawable.baseline_egg)
        imageView.translationZ = 10f

        val animation = AnimationUtils.loadAnimation(this, R.anim.throw_egg)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                imageView.setImageResource(R.drawable.baseline_broke_egg)
                imageView.postDelayed({
                    startQuizBinding.startQuizContainer.removeView(imageView)
                }, 1000)
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        startQuizBinding.startQuizContainer.addView(imageView,3)
        imageView.startAnimation(animation)
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
        try{
            socket.connect()
            socket.on(Socket.EVENT_DISCONNECT, disconnectReturn)
            socket.on(Socket.EVENT_CONNECT_ERROR, connectErrorReturn)
            socket.on(Socket.EVENT_CONNECT, connectReturn)

        } catch (e: URISyntaxException) {
            Log.d("error in connecting occur\n\n\n\n\n", e.toString())
            e.printStackTrace()
        }
    }
    private fun joinQuiz(){
        socket.on("joinQuiz", joinReturn)
        socket.emit("join_quiz", quizId, Constants.userId)
    }
    private fun startQuiz(){
        socket.emit("start_quiz", quizId, questionList[0]._id, questionList.size)
    }
    private fun finishQuestion(){
        val nextId = if((currentAtQuestion+1) < questionList.size) questionList[currentAtQuestion+1]._id else null
        socket.emit("finish_question", quizId, Constants.userId, questionList[currentAtQuestion].questionType,
            userAnsOptions[currentAtQuestion], currentQuestionScore, nextId)
    }
    private fun timeOut(){
        val nextId = if((currentAtQuestion+1) < questionList.size) questionList[currentAtQuestion+1]._id else null
        socket.emit("timeout", quizId, Constants.userId, questionList[currentAtQuestion].questionType, nextId)
    }
    private fun finishQuiz(){
        socket.emit("finish_quiz", quizId)
    }
    private fun returnQuizState(){
        requestState = 0
        socket.emit("return_quiz_state", quizId)
    }
    private fun attackMember(receiverID: String, attackType: Int){
        Log.d("type $attackType , now attacking", receiverID)
        socket.emit("use_ability", quizId, Constants.userId, receiverID, attackType)
    }
    private val connectReturn = Emitter.Listener {args->
        for(i in args.indices){
            Log.d("connect return $i is", args[i].toString())
        }
        joinQuiz()
    }
    private val joinReturn = Emitter.Listener { args->
        quizMemberNumber = args[0] as Int
        socket.on("startQuiz", startReturn)

        Log.d("join successful member num is",args[0].toString())
        quizMembers.clear()
        val membersArr = args[1] as JSONObject
        Log.d("all member is", args[1].toString())
        for(memberKey in membersArr.keys()){
            val memberName = membersArr.getString(memberKey)
            memberIDToName[memberKey] = memberName
            val joinMember = QuizMember(memberName, 0, memberKey, 0, ArrayList())
            quizMembers.add(joinMember)
        }

        runOnUiThread {
            memberInLobbyAdapter.notifyDataSetChanged()
        }
    }
    private val startReturn = Emitter.Listener { args->
        if(requestStart==0) {
            Log.d("start successful", args.toString())
            requestStart = 1
            socket.on("finishQuestion", finishQuestionReturn)
            socket.on("timeout", timeOutReturn)
            socket.on("finishQuiz", finishQuizReturn)
            socket.on("returnQuizState", quizStateReturn)
            socket.on("useAbility", attackReturn)
            runOnUiThread {
                startQuizBinding.startQuizContainer.visibility = View.VISIBLE
                startMusic()
                setQuestion()
                lobbyDialog.dismiss()
            }
        }
    }
    private val finishQuestionReturn = Emitter.Listener { args->
        Log.d("finish Question successful",args.toString())
        for(i in args.indices){
            isNext = args[i] as Boolean
        }
        runOnUiThread {

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
        runOnUiThread {
        }
    }
    private val quizStateReturn = Emitter.Listener { args->
        if(requestState==0) {
            requestState+=1
            quizMembers.clear()
            val returnState = args[0] as JSONObject
            val userStateString = returnState.getJSONObject("userStates")
            Log.d("userStateString is", userStateString.toString())
            for (userID in userStateString.keys()) {
                val totalRecords = ArrayList<ArrayList<String>>()
                val userstate = userStateString.getJSONObject(userID)
                val userRecord = userstate.getJSONObject("records")
                val userScore = userstate.getInt("score").toInt()
                val userName = memberIDToName[userID] ?: userID.substring(0, 3)
//                Log.d("userRecord = ", userRecord.toString())
//                Log.d("user Score =", userScore.toString())
                for (qID in userRecord.keys()) {
                    val singleRecord = ArrayList<String>()
                    if (!userRecord.isNull(qID)) {
                        val tmpRecord = userRecord.getJSONArray(qID)
                        for (index in 0 until tmpRecord.length()) {
                            singleRecord.add(tmpRecord[index].toString())
                        }
                    }
                    totalRecords.add(singleRecord)
                }
                val joinMember = QuizMember(userName, userScore * singleQuestionScore, userID, userScore, totalRecords)
                quizMembers.add(joinMember)
            }
            quizMembers.sortByDescending { it.correctAnswerNum }
            runOnUiThread {
                onReturnStateListener?.onReturn()
            }
        }
    }
    private val attackReturn = Emitter.Listener { args ->
        Log.d("attack return successful", "")
        for(i in args.indices){
            Log.d("attack return $i is", args[i].toString())
        }

        if(args[0].toString() == Constants.userId){
            runOnUiThread {
                val attackType = args[2] as Int
                val attacker = args[1] as String
                val attackerName = memberIDToName[attacker] ?: "unknown"
                when (attackType) {
                    0 -> {
                        shakeAnim()
                        Toast.makeText(this, "你被${attackerName}翻考卷的氣勢嚇到了!", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        hookAnim()
                        if(questionList[currentAtQuestion].questionType == Constants.questionTypeTrueOrFalse){
                            if(this::textViewTrue.isInitialized){
                                textViewTrue.isClickable = false
                                textViewTrue.setBackgroundColor(Color.parseColor("#D0D0D0"))
                                if(currentAnswer=="true"){
                                    currentAnswer = ""
                                    trueOrFalseSelected = false
                                }
                            }
                        }else{
                                if(this::optionAdapter.isInitialized){
                                    optionAdapter.steal()
                                }
                        }
                        Toast.makeText(this, "你被${attackerName}偷走了一個選項!", Toast.LENGTH_SHORT).show()

                    }
                    2 -> {
                        throwEggAnim()
                        Toast.makeText(this, "你被${attackerName}用雞蛋糊了一臉!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    private val disconnectReturn = Emitter.Listener { args->
        Log.d("disconnect return ", "")
        runOnUiThread {
        }
    }
    private val connectErrorReturn = Emitter.Listener { args->
        Log.d("connect error return is", args.toString())
        for(i in args.indices){
            Log.d("connect error return $i is", args[i].toString())
        }
        runOnUiThread {
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
            socket.off("useAbility")
        }
        if(this::player.isInitialized){
            player.stop()
        }
    }
}