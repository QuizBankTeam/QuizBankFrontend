package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
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
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.databinding.ActivitySingleQuestionBinding
import com.example.quizbanktest.fragment.SingleQuizPage
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.utils.Constants


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
    private var answerOptionInt = ArrayList<Int>()  //為正確答案的position
    private lateinit var quizType: String
    private lateinit var answerDescriptionView: TextView
    private lateinit var optionAdapter: OptionAdapter
    private var selectedView =  ArrayList<View>()
    private var imageArr = ArrayList<Bitmap>()
    private var questionIndex = 0
    private val openPhotoAlbum:Int = 1314
    private var resolver: ContentResolver? = null
    private var imageChange = false
    private val imageStatus_noImage = "noImage"
    private val imageStatus_existImage = "existImage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionBinding = ActivitySingleQuestionBinding.inflate(layoutInflater)
        setContentView(questionBinding.root)
        init()
        resolver = this.contentResolver

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
            if(optionlist.size>=Constants.optionNum.size){
                questionBinding.optionAdd.visibility = View.GONE
            }

            //修改選項
            optionAdapter.setSelectClickListener(object: OptionAdapter.SelectOnClickListener{
                override fun onclick(position: Int, holder: OptionAdapter.MyViewHolder) {
                    optionChange(position, holder)
                }
            })

            //新增選項
            questionBinding.optionAdd.setOnClickListener {
                addOption()
            }
        }
        else if(questionType=="ShortAnswer"){
            questionBinding.questionContainer.removeView(questionBinding.optionScrollView)
            initShortAnswer()
        }
        else if(questionType=="TrueOrFalse"){
            questionBinding.questionContainer.removeView(questionBinding.optionScrollView)
            initTrueOrFalse()
        }
        else{
            questionBinding.questionContainer.removeView(questionBinding.optionScrollView)
        }

        //修改圖片
        questionBinding.editImage.setOnClickListener {
            editImage()
        }

        //新增圖片
        questionBinding.addImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, openPhotoAlbum)
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
        if(resultCode == Constants.RESULT_DELETE) {  //刪除題目
            val intent = Intent()
            setResult(Constants.RESULT_DELETE, intent)
            finish()
        }
        else if(resultCode == RESULT_OK) {
            if (data != null) {
                if(requestCode == openPhotoAlbum){ //從圖片庫回傳
                    val dataUri: Uri? = data.data
                    Log.d(
                        "mengyuanuri",
                        "uri:" + dataUri!!.scheme + ":" + dataUri.schemeSpecificPart)
                    imageChange = true
                    val bitmap = BitmapFactory.decodeStream(resolver!!.openInputStream(dataUri))
                    if(imageArr.isNotEmpty()){
                        imageArr[0] = bitmap
                    }else{
                        imageArr.add(bitmap)
                    }
                    editImageContainer(imageStatus_existImage)
                }
                else{                             //修改題目設定
                    this.questionTitle = data.getStringExtra("Key_title").toString()
                    this.questionAnswerDescription = data.getStringExtra("Key_answerDescription").toString()
                    this.questionNumber = data.getStringExtra("Key_number").toString()
                    this.questionType = if(questionType=="MultipleChoiceS" || questionType=="MultipleChoiceM")
                        data.getStringExtra("Key_type").toString() else this.questionType
                    questionBinding.QuestionTitle.text = this.questionTitle

                    questionBinding.QuestionType.text = if(questionType==Constants.questionTypeMultipleChoiceS) "單選"
                    else if(questionType==Constants.questionTypeMultipleChoiceM) "多選"
                    else if(questionType==Constants.questionTypeTrueOrFalse) "是非"
                    else if(questionType==Constants.questionTypeShortAnswer) "簡答"
                    else "填充"
                }
            }
        }
        else{  //啥都沒改

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
        val questionIndex = intent.getIntExtra("question_index", 0)
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
        if(quizType==Constants.quizTypeCasual){
            val timeLimit = intent.getIntExtra("Key_timeLimit",0).toString() + "秒"
            this.time_limit = intent.getIntExtra("Key_timeLimit",0)
            questionBinding.timeLimit.text = timeLimit
        }else{
            questionBinding.upperFrame.removeView(questionBinding.timeLimit)
        }
        if(questionIndex<SingleQuiz.Companion.quizImages.size) {
            for (item in SingleQuiz.Companion.quizImages[questionIndex]) {
                val imageBytes: ByteArray = Base64.decode(item, Base64.DEFAULT)
                val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageArr.add(decodeImage)
            }
        }else{
            Toast.makeText(this, "quizImage index超出範圍 該題沒有圖片", Toast.LENGTH_LONG).show()
        }
        if(imageArr.isNotEmpty()) {
            editImageContainer(imageStatus_existImage)
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
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
        val marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
        val marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics).toInt()
        val tagHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics).toInt()
        val tagWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, resources.displayMetrics).toInt()
        val layoutParam = LinearLayout.LayoutParams(tagWidth, tagHeight)
        layoutParam.marginStart = marginHorizontal
        layoutParam.marginEnd = marginHorizontal
        layoutParam.topMargin = marginTop

        for(i in 0 until tag.size){
            if(i>2){ break }
            val tagTextView = TextView(this)
            tagTextView.isClickable = true
            tagTextView.text = tag[i]
            tagTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_tag, 0, 0, 0)
            tagTextView.setBackgroundResource(R.drawable.corner_radius_blue)
            tagTextView.setPadding(padding)
            tagTextView.layoutParams = layoutParam
            tagTextView.gravity = Gravity.CENTER
            tagTextView.setTextColor(Color.WHITE)
            tagTextView.textSize = textSize
            tagTextView.ellipsize = TextUtils.TruncateAt.END
            questionBinding.QuestionTags.addView(tagTextView) //QuestionTags is a container of questionTag
            tmpTag.add(tag[i])
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

        if (questionType==Constants.questionTypeMultipleChoiceS || questionType==Constants.questionTypeMultipleChoiceM){
            getOptions() //要先get option底下的answer options才不會出錯...
            for (i in answerOptionInt)
                answerOptionListStr.add(optionListStr[i])
        }else if(questionType!=Constants.questionTypeShortAnswer){
            answerOptionListStr = answerOptions
        }

        if(questionType==Constants.questionTypeMultipleChoiceS && answerOptionInt.size>1){
            AlertDialog.Builder(this).setTitle("單選題只能有一個正確選項!").setPositiveButton("我懂", null).show()
        }else {
            if(imageArr.isNotEmpty()){
                val imageBase64 = Constants.bitmapToString(imageArr[0])
                if(SingleQuiz.Companion.quizImages[questionIndex].isEmpty()){
                    SingleQuiz.Companion.quizImages[questionIndex].add(imageBase64!!)
                }else{
                    SingleQuiz.Companion.quizImages[questionIndex][0] = imageBase64!!
                }
            }
            intent.putStringArrayListExtra("Key_options", optionListStr)
            intent.putExtra("Key_description", questionBinding.questionDescription.text)
            intent.putExtra("Key_title", questionTitle)
            intent.putExtra("Key_answerDescription", questionAnswerDescription)
            intent.putExtra("Key_number", questionNumber)
            intent.putExtra("Key_type", questionType)
            intent.putExtra("Image_change", imageChange)
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

    private fun addOption(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_option, null)
        val deleteOrAddBtn: ImageButton = v.findViewById(R.id.option_deleteOrAdd)
        val optionNum: TextView = v.findViewById(R.id.optionNum)
        val ansSwitch: SwitchCompat = v.findViewById(R.id.answer_switch)
        val editOption:TextView = v.findViewById(R.id.optionContent)
        var isCorrect = false
        val needToChange = ArrayList<Int>()
        optionNum.text = getString(R.string.Question_add_option)
        deleteOrAddBtn.setImageResource(R.drawable.baseline_add_circle_24)
        ansSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isCorrect = isChecked
        }
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()

        deleteOrAddBtn.setOnClickListener {
            val tmpText =  editOption.text.toString()
            if(tmpText.isNotEmpty()){
                val tmpOption = Option(Constants.optionNum[optionlist.size], tmpText)
                optionlist.add(tmpOption)
                if(questionType==Constants.questionTypeMultipleChoiceS && isCorrect){
                    answerOptionInt.clear()
                    answerOptionInt.add(optionlist.size-1)
                    optionAdapter.setAnswerOptions(answerOptionInt)
                }
                if(optionlist.size>=Constants.optionNum.size){
                    questionBinding.optionAdd.visibility = View.GONE
                }
                optionAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }
    }

    private fun optionChange(position: Int, holder: OptionAdapter.MyViewHolder) {
        val currentOption = optionlist[position]
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_option, null)
        val needToChange = ArrayList<Int>()
        var isDeleted = false
        var originStatus = false
        if(answerOptionInt.contains(position)){  //正確答案中有這個被選取的選項
            originStatus = true
        }

        builder.setView(v)
        val optionDialog: AlertDialog = builder.create()
        optionDialog.show()
        var newStatus = originStatus
        val editOption:TextView = v.findViewById(R.id.optionContent)
        val ansSwitch: SwitchCompat = v.findViewById(R.id.answer_switch)
        val deleteOrAddBtn: ImageButton = v.findViewById(R.id.option_deleteOrAdd)
        val optionNum: TextView = v.findViewById(R.id.optionNum)
        optionNum.text = currentOption.optionNum
        editOption.text = currentOption.optionContent
        ansSwitch.isChecked = newStatus
        ansSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            newStatus = isChecked
        }
        deleteOrAddBtn.setOnClickListener {
            val deleteBuilder = AlertDialog.Builder(this)
            deleteBuilder.setTitle("確定刪除選項?")
            deleteBuilder.setPositiveButton("確定") { dialog, which ->
                isDeleted = true
                optionDialog.dismiss()
            }
            deleteBuilder.setNegativeButton("取消"){ dialog, which ->
            }
            deleteBuilder.show()
        }

        optionDialog.setOnDismissListener {
            if(!isDeleted){
                val tmpText =  editOption.text
                optionlist[position].optionContent = tmpText.toString() //選項內的文字修改
                if(originStatus!=newStatus) {
                    if(newStatus){ //原先不是正確答案 後來是
                        if (this.questionType == "MultipleChoiceS") {
                            if(answerOptionInt.isNotEmpty()){
                                needToChange.add(answerOptionInt[0])
                                answerOptionInt.clear()
                            }
                        }
                        answerOptionInt.add(position)
                    }else{  //原先是正確答案 後來不是
                        if(answerOptionInt.size > 1)
                            answerOptionInt.remove(position)
                        else {
                            AlertDialog.Builder(this).setTitle("至少要有一個正確選項!")
                                .setPositiveButton("我懂", null).show()
                        }
                    }
                }
                optionAdapter.setAnswerOptions(answerOptionInt)
                needToChange.add(position)
                optionAdapter.itemChange(needToChange)
            }else{
                answerOptionInt.remove(position)
                optionlist.removeAt(position)
                for(answerIndex in answerOptionInt.indices){
                    if(position<answerOptionInt[answerIndex]){
                        answerOptionInt[answerIndex] -= 1
                    }
                }
                for(index in position until optionlist.size){
                    optionlist[index].optionNum = Constants.optionNum[index]
                }
                optionAdapter.setAnswerOptions(answerOptionInt)
                optionAdapter.notifyDataSetChanged()
                questionBinding.optionAdd.visibility = View.VISIBLE
            }
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
            this.questionTag[position] = editTag.text.toString()
            this.questionTagTextView[position].text = this.questionTag[position]
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

    private fun editImage(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_question_image, questionBinding.questionContainer,false)
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        val questionImage: ImageView = v.findViewById(R.id.question_image)
        val addImageBtn: TextView = v.findViewById(R.id.add_image)
        val editImageBtn: TextView = v.findViewById(R.id.edit_image)
        val deleteImageBtn: TextView = v.findViewById(R.id.delete_image)
        questionImage.setImageBitmap(imageArr[0])
        editImageBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, openPhotoAlbum)
            dialog.dismiss()
        }
        deleteImageBtn.setOnClickListener {
            editImageContainer(imageStatus_noImage)
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun editImageContainer(imageStatus: String){
        if(imageStatus==imageStatus_noImage){
            questionBinding.upperFrame.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85f, resources.displayMetrics).toInt()
            questionBinding.QuestionImage.setImageResource(0)
            questionBinding.QuestionImage.setBackgroundResource(R.drawable.background_gray)
            questionBinding.QuestionImage.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
            questionBinding.editImage.visibility = View.GONE
            questionBinding.addImageContainer.visibility = View.VISIBLE
        }
        else if(imageStatus==imageStatus_existImage){
            questionBinding.upperFrame.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230f, resources.displayMetrics).toInt()
            questionBinding.QuestionImage.setImageBitmap(imageArr[0])
            questionBinding.QuestionImage.setBackgroundResource(0)
            questionBinding.QuestionImage.layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics).toInt()
            questionBinding.editImage.visibility = View.VISIBLE
            questionBinding.addImageContainer.visibility = View.GONE
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
        val tmpAnswerOptionInt : ArrayList<Int> = ArrayList()

        if (optionListStr.isNotEmpty()) {
            for(i in optionListStr.indices) {
                if(i==Constants.optionNum.size-1) break
                val tmpOption = Option(Constants.optionNum[i], optionListStr[i])
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

