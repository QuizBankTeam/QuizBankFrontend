package com.example.quizbanktest.activity.quiz
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.databinding.ActivitySingleQuestionBinding
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.fragment.SingleQuizPage

class SingleQuestion : AppCompatActivity(){
    private lateinit var questionBinding: ActivitySingleQuestionBinding
    private  var optionlist : ArrayList<Option> = ArrayList()
    private lateinit var questionTag : ArrayList<String>
    private lateinit var answerOptions : ArrayList<String>
    private var questionTagTextView : ArrayList<TextView> = ArrayList()
    private lateinit var optionListStr: ArrayList<String>
    private var time_limit = -1
    private var questionDescription: String = ""
    private var questionType : String = ""
    private lateinit var questionTitle: String
    private lateinit var questionAnswerDescription: String
    private lateinit var questionNumber: String
    private lateinit var questionBank: String
    private lateinit var questionProvider : String
    private lateinit var questionCreatedDate : String
    private lateinit var answerOptionInt : ArrayList<Int>  //為正確答案的position
    private lateinit var quizType: String
    private lateinit var answerDescriptionView: TextView
    private lateinit var optionAdapter: OptionAdapter
    private var selectedView =  ArrayList<View>()
    private var imageArr = ArrayList<Bitmap>()
    private var quizIndex = 0
    private var questionIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionBinding = ActivitySingleQuestionBinding.inflate(layoutInflater)
        setContentView(questionBinding.root)
        init()

        if(questionType=="MultipleChoiceS" || questionType=="MultipleChoiceM"){
            initMultiChoice()
            questionBinding.QuestionOption.layoutManager = LinearLayoutManager(this)
            questionBinding.QuestionOption.setHasFixedSize(true)
            optionAdapter = OptionAdapter(this, optionlist)
            questionBinding.QuestionOption.adapter = optionAdapter
            optionAdapter.setInSingleQuestion(true)
            questionBinding.QuestionOption.post{    //將正確選項的背景改為綠色
                optionAdapter.setAnswerOptions(answerOptionInt)
                optionAdapter.itemChange(answerOptionInt)
            }


            //修改選項
            optionAdapter.setSelectClickListener(object: OptionAdapter.SelectOnClickListener{
                override fun onclick(position: Int, holder: OptionAdapter.MyViewHolder) {
                    optionChange(position, holder)
                }
            })
        }
        else if(questionType=="ShortAnswer"){
            questionBinding.questionContainer.removeView(questionBinding.QuestionOption)
            initShortAnswer()
        }
        else if(questionType=="TrueOrFalse"){
            questionBinding.questionContainer.removeView(questionBinding.QuestionOption)
            initTrueOrFalse()
        }
        else{
            questionBinding.questionContainer.removeView(questionBinding.QuestionOption)
        }


        //回傳修改的內容至 singleQuiz
        questionBinding.backBtn.setOnClickListener {
            backBtn()
        }

        //修改答題時長
        if(quizType=="casual") {
            questionBinding.timeLimit.setOnClickListener {
                selectTimeLimit()
            }
        }

        //修改題目簡介
        questionBinding.questionDescription.setOnClickListener {
            descriptionChange()
        }

        //修改標籤
        questionTagTextView.clear()
        for(i in 0 until questionTag.count()){
            questionTagTextView.add(questionBinding.QuestionTags[i] as TextView)
            questionTagTextView[i].setOnClickListener {
                tagChange(questionTag[i], i)
            }
        }

        //修改題目設定
        questionBinding.questionSetting.setOnClickListener {
            questionSetting()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //從single question setting傳回
        if(resultCode == RESULT_CANCELED) {  //刪除題目
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        else if(resultCode == RESULT_OK) {   //修改題目設定
            Toast.makeText(this, "modify something", Toast.LENGTH_SHORT).show()
            if (data != null) {
                this.questionTitle = data.getStringExtra("Key_title").toString()
                this.questionAnswerDescription = data.getStringExtra("Key_answerDescription").toString()
                this.questionNumber = data.getStringExtra("Key_number").toString()
                this.questionType = if(questionType=="MultipleChoiceS" || questionType=="MultipleChoiceM")
                                        data.getStringExtra("Key_type").toString() else this.questionType
                questionBinding.QuestionTitle.text = this.questionTitle
            }
        }
        else{                               //啥都沒改
            Toast.makeText(this, "modify nothing", Toast.LENGTH_SHORT).show()
        }
    }
    private fun init()
    {

        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val image = intent.getStringArrayListExtra("Key_image")
        val options = intent.getStringArrayListExtra("Key_options")
        val tag = intent.getStringArrayListExtra("Key_tag")
        val description = intent.getStringExtra("Key_description")
        val number = intent.getStringExtra("Key_number")
        val questionBank = intent.getStringExtra("Key_questionBank")
        val provider = intent.getStringExtra("Key_provider")
        val createdDate = intent.getStringExtra("Key_createdDate")
        val answerOption = intent.getStringArrayListExtra("Key_answerOptions")
        val answerDescription = intent.getStringExtra("Key_answerDescription")
        val quizType = intent.getStringExtra("Key_quizType")
        val quizIndex = intent.getIntExtra("quiz_index", 0)
        val questionIndex = intent.getIntExtra("question_index", 0)
        this.quizIndex = quizIndex
        this.questionIndex = questionIndex
        optionlist.clear()


        if(description!=null)
            this.questionDescription = description
        if (type != null)
            this.questionType = type
        if (title != null)
            this.questionTitle = title
        if (answerDescription != null)
            this.questionAnswerDescription = answerDescription
        if (number != null)
            this.questionNumber = number
        if (questionBank != null)
            this.questionBank = questionBank
        if (provider != null)
            this.questionProvider = provider
        if (createdDate != null)
            this.questionCreatedDate = createdDate
        if (quizType != null)
            this.quizType = quizType
        if (answerOption != null)
            this.answerOptions = answerOption
        if (options != null){
            optionListStr = options
        }
        if(quizType=="casual"){
            val timeLimit = intent.getIntExtra("Key_timeLimit",0).toString() + "秒"
            this.time_limit = intent.getIntExtra("Key_timeLimit",0)
            questionBinding.timeLimit.text = timeLimit
        }else{
            questionBinding.containerTimeLimit.removeView(questionBinding.timeLimit)
        }

        for(item in SingleQuizPage.Companion.quizListImages[quizIndex][questionIndex]){
            val tmpImageStr: String? = item.get()
            if(tmpImageStr!=null){
                val imageBytes: ByteArray = Base64.decode(tmpImageStr, Base64.DEFAULT)
                val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageArr.add(decodeImage)
                Log.d("tmpImageStr", "is not null ")
            }
            Log.d("quiz index = $quizIndex", "question index = $questionIndex")
        }
        if(imageArr.isNotEmpty()) {
            Log.d("setting image", "question index ")
            questionBinding.QuestionImage.setImageBitmap(imageArr[0])
        }
        questionBinding.questionDescription.text = description
        questionBinding.QuestionTitle.text = title
        when(type){
            "MultipleChoiceS" -> {
                questionBinding.QuestionType.text = "單選"
            }
            "MultipleChoiceM" -> {
                questionBinding.QuestionType.text = "多選"
            }
            "ShortAnswer" -> {
                questionBinding.QuestionType.text = "簡答"
            }
            "TrueOrFalse" -> {
                questionBinding.QuestionType.text = "是非"
            }
            "Filling" -> {
                questionBinding.QuestionType.text = "填空"
            }
        }

        //設定(build)tag
        if(tag != null){
            initTag(tag)
        }
    }

    private fun initTag(tag: ArrayList<String>){
        //設定tag
        val tmpTag : ArrayList<String> = ArrayList()
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
        val textSize1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
        val marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
        val marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.marginStart = marginHorizontal
        layoutParam.marginEnd = marginHorizontal
        layoutParam.topMargin = marginTop

        if(tag.size>0) {
            val tagTextView = TextView(this)
            tagTextView.isClickable = true
            tagTextView.text = tag[0]
            tagTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_tag, 0, 0, 0)
            tagTextView.setBackgroundResource(R.drawable.corner_radius_blue)
            tagTextView.setPadding(padding)
            tagTextView.layoutParams = layoutParam
            tagTextView.gravity = Gravity.CENTER
            tagTextView.setTextColor(Color.WHITE)
            tagTextView.textSize = textSize1
            questionBinding.QuestionTags.addView(tagTextView)
            tmpTag.add(tag[0]) //QuestionTags is a container of questionTag
        }
        if(tag.size>1) {
            val tagTextView1 = TextView(this)
            tagTextView1.isClickable = true
            tagTextView1.text = tag[1]
            tagTextView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_tag, 0, 0, 0)
            tagTextView1.setBackgroundResource(R.drawable.corner_radius_blue)
            tagTextView1.setPadding(padding)
            tagTextView1.layoutParams = layoutParam
            tagTextView1.gravity = Gravity.CENTER
            tagTextView1.setTextColor(Color.WHITE)
            tagTextView1.textSize = textSize1
            questionBinding.QuestionTags.addView(tagTextView1)
            tmpTag.add(tag[1])
        }
        if(tag.size>2){
            val tagTextView2 = TextView(this)
            tagTextView2.isClickable = true
            tagTextView2.text = tag[2]
            tagTextView2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_tag, 0, 0, 0)
            tagTextView2.setBackgroundResource(R.drawable.corner_radius_blue)
            tagTextView2.setPadding(padding)
            tagTextView2.layoutParams = layoutParam
            tagTextView2.gravity = Gravity.CENTER
            tagTextView2.setTextColor(Color.WHITE)
            tagTextView2.textSize = textSize1
            questionBinding.QuestionTags.addView(tagTextView2)
            tmpTag.add(tag[2])
        }
        this.questionTag = tmpTag
    }
    private fun getOptions(){
        val tmpList: ArrayList<String> = ArrayList()
        for (item in this.optionlist) {
            tmpList.add(item.optionContent)
        }
        optionListStr = tmpList
    }

    private fun backBtn(){
        val intent = Intent()
        var answerOptionListStr: ArrayList<String> = ArrayList()
        if (this.questionType=="MultipleChoiceS" || this.questionType=="MultipleChoiceM"){
            for (i in answerOptionInt)
                answerOptionListStr.add(optionListStr[i])
        }else if(this.questionType!="ShortAnswer"){
            answerOptionListStr = answerOptions
        }

        if(this.questionType=="MultipleChoiceS" && answerOptionInt.size>1){
            AlertDialog.Builder(this).setTitle("單選題只能有一個正確選項!").setPositiveButton("我懂", null).show()
        }else {
            getOptions()
            intent.putStringArrayListExtra("Key_options", optionListStr)
            intent.putExtra("Key_description", questionBinding.questionDescription.text)
            intent.putExtra("Key_title", questionTitle)
            intent.putExtra("Key_answerDescription", questionAnswerDescription)
            intent.putExtra("Key_number", questionNumber)
            intent.putExtra("Key_type", questionType)
            for (i in 0 until this.questionTag.size) {
                val name = "Key_tag$i"
                intent.putExtra(name, this.questionTag[i])
            }

            intent.putStringArrayListExtra("Key_answerOptions", answerOptionListStr)
            if(quizType=="casual") {
                intent.putExtra("Key_timeLimit", time_limit)
            }
            intent.putExtra("Key_tagNum", this.questionTag.size)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun optionChange(position: Int, holder: OptionAdapter.MyViewHolder) {
        val currentOption = optionlist[position]
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_option, null)
        val needToChange = ArrayList<Int>()
        var status = false
        var answerOptionIndices = -1
        for(i in answerOptionInt.indices){
            if(answerOptionInt[i] == position) {
                status = true
                answerOptionIndices = i
            }
        }
        builder.setTitle(currentOption.optionNum)
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()

        val editOption:TextView = v.findViewById(R.id.optionContent)
        val ansSwitch: SwitchCompat = v.findViewById(R.id.answer_switch)
        editOption.text = currentOption.optionContent
        ansSwitch.isChecked = status
        ansSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            status = isChecked
        }

        dialog.setOnDismissListener {
            val tmpText =  editOption.text
            currentOption.optionContent = tmpText.toString()
            if(status) {
                if(answerOptionIndices==-1) { //原先不是正確答案 後來是
                    if (this.questionType == "MultipleChoiceS") {
                        needToChange.add(answerOptionInt[0])
                        answerOptionInt.clear()
                    }
                    answerOptionInt.add(position)
                }
            }
            else{
                if(answerOptionIndices!=-1) {  //原先是正確答案 後來不是
                    if(answerOptionInt.size > 1)
                        answerOptionInt.removeAt(answerOptionIndices)
                    else {
                        AlertDialog.Builder(this).setTitle("至少要有一個正確選項!")
                            .setPositiveButton("我懂", null).show()
                    }
                }
            }
            optionAdapter.setAnswerOptions(answerOptionInt)
            needToChange.add(position)
            optionAdapter.itemChange(needToChange)
        }
    }

    private fun selectTimeLimit(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_select_time_limit, null)
        val selectSpinner : Spinner = v.findViewById(R.id.Select_time_limit)
        val limitOptions = arrayOf("10秒", "15秒", "20秒", "30秒", "60秒")
        val limitOptionsInt = intArrayOf(10, 15, 20, 30, 60)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, limitOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectSpinner.adapter = adapter
        if(time_limit in limitOptionsInt){
            selectSpinner.setSelection(limitOptionsInt.indexOf(time_limit))
        }
        builder.setTitle("答題時長")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()

        selectSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                time_limit = limitOptionsInt[position]
                questionBinding.timeLimit.text = limitOptions[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun descriptionChange(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_question_description, null)
        val editDescirption: EditText = v.findViewById(R.id.edit_question_description)
        editDescirption.setText(this.questionDescription)
        builder.setTitle("題目敘述")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setOnDismissListener {
            this.questionDescription = editDescirption.text.toString()
            questionBinding.questionDescription.text = this.questionDescription
        }
    }

    private fun tagChange(tmpText : String, position : Int){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_question_tag, null)
        val editTag: EditText = v.findViewById(R.id.edit_question_tag)
        editTag.setText(tmpText)
        builder.setTitle("題目標籤")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setOnDismissListener {
            if(editTag.text.toString().length>5){
                AlertDialog.Builder(this).setTitle("標籤過長5!").setPositiveButton("我懂", null).show()
            }else{
                this.questionTag[position] = editTag.text.toString()
                this.questionTagTextView[position].text = this.questionTag[position]
            }
        }
    }

    private fun answerDescriptionChange(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_question_description, null)
        val editDescription: EditText = v.findViewById(R.id.edit_question_description)
        editDescription.setText(this.questionDescription)
        builder.setTitle("答案敘述")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setOnDismissListener {
            this.questionAnswerDescription = editDescription.text.toString()
            this.answerDescriptionView.text = this.questionAnswerDescription
        }
    }
    private fun questionSetting(){
        val intent = Intent()
        intent.setClass(this@SingleQuestion, SingleQuestionSetting::class.java)
        intent.putExtra("Key_title", questionTitle)
        intent.putExtra("Key_answerDescription", questionAnswerDescription)
        intent.putExtra("Key_number", questionNumber)
        intent.putExtra("Key_type", questionType)
        intent.putExtra("Key_questionBank", questionBank)
        intent.putExtra("Key_provider", questionProvider)
        intent.putExtra("Key_createdDate", questionCreatedDate)
        startActivityForResult(intent, 1001)
    }

    private fun initShortAnswer(){
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
        textView.id = View.generateViewId()
        textView.isClickable = true
        textView.text = this.questionAnswerDescription
        textView.setBackgroundResource(R.drawable.textview_answer_border)
        textView.setPadding(padding)
        textView.layoutParams = layoutParam
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.textSize = textSize
        textView.minHeight = answerMinH
        textView.hint = "輕觸輸入答案敘述"
        questionBinding.questionContainer.addView(textView)
        questionBinding.questionDescription.minHeight = descriptionMinH
        val answerDescriptionView = questionBinding.root.findViewById<TextView>(textView.id)
        this.answerDescriptionView = answerDescriptionView
        answerDescriptionView.setOnClickListener {
            answerDescriptionChange()
        }
    }

    private fun initTrueOrFalse(){
        val v:View =  layoutInflater.inflate(R.layout.item_option_trueorfalse, questionBinding.questionContainer, false)
        val textViewTrue : TextView = v.findViewById(R.id.option_true)
        val textViewFalse: TextView = v.findViewById(R.id.option_false)

        if(answerOptions[0] == "true"){
            textViewTrue.setBackgroundColor( ContextCompat.getColor(this, R.color.answer_correct) )
        }else{
            textViewFalse.setBackgroundColor( ContextCompat.getColor(this, R.color.answer_correct) )
        }

        textViewTrue.setOnClickListener {
            textViewTrue.setBackgroundColor( ContextCompat.getColor(this, R.color.answer_correct) )
            textViewFalse.setBackgroundColor( 0 )
            answerOptions[0] = "true"
        }
        textViewFalse.setOnClickListener {
            textViewTrue.setBackgroundColor( 0 )
            textViewFalse.setBackgroundColor( ContextCompat.getColor(this, R.color.answer_correct) )
            answerOptions[0] = "false"
        }
        questionBinding.questionContainer.addView(v)
    }
    private fun initMultiChoice(){
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F", "G", "H")
        val tmpAnswerOptionInt : ArrayList<Int> = ArrayList()

        if (optionListStr.isNotEmpty()) {
            for(i in optionListStr.indices) {
                val tmpOption = Option(optionNum[i], optionListStr[i])
                optionlist.add(tmpOption)

                if (this.answerOptions.isNotEmpty()) {  //找到option中對的選項
                    for(item in answerOptions)
                        if(optionListStr[i]==item)
                            tmpAnswerOptionInt.add(i)
                }
            }
            answerOptionInt = tmpAnswerOptionInt
        }
    }
}

//answer description
//<TextView
//android:id="@+id/question_answerDescription"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:minHeight="150dp"
//android:clickable="true"
//android:layout_marginHorizontal="20dp"
//android:layout_marginTop="20dp"
//android:layout_marginBottom="10dp"
//android:background="@drawable/textview_answer_border"
//android:padding="10dp"
//android:gravity="center_vertical"
//android:text="圖一為 2008 年的推估結果，顯示臺灣人口從 1960-2060 年間出生率與死亡率
//的預測變化，依圖所示，在不考慮社會增加率的情況下，下列何者最為正確？"
//android:textSize="14dp"
//android:textColor="@color/white"/>

//tag
//<TextView
//android:id="@+id/Question_tag0"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:padding="5dp"
//android:layout_marginHorizontal="3dp"
//android:gravity="center"
//android:text="2009"
//android:textSize="15dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>

