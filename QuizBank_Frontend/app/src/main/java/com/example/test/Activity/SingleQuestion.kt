package com.example.test.Activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import com.example.test.Adapter.OptionAdapter
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.*
import com.example.test.R
import com.example.test.databinding.SingleQuestionBinding
import com.example.test.model.Option

class SingleQuestion : AppCompatActivity(){
    private lateinit var questionBinding: SingleQuestionBinding
    private  var optionlist : ArrayList<Option> = ArrayList()
    private lateinit var questionTag : ArrayList<String>
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionBinding = SingleQuestionBinding.inflate(layoutInflater)
        setContentView(questionBinding.root)
        init()
        val adapter = OptionAdapter(this, optionlist)
        questionBinding.QuestionOption.adapter = adapter
        questionBinding.QuestionOption.post { //將正確選項的背景改為綠色
            adapter.setAnswerOptions(answerOptionInt)
        }

        //修改選項
        questionBinding.QuestionOption.setOnItemClickListener { parent, view, position, id ->
            optionChange(position, adapter)
        }

        //回傳修改的內容至 singleQuiz
        questionBinding.backBtn.setOnClickListener {
            backBtn()
        }

        //修改答題時長
        questionBinding.timeLimit.setOnClickListener {
            selectTimeLimit()
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
        if(resultCode == RESULT_CANCELED)
            Toast.makeText(this, "modify nothing", Toast.LENGTH_SHORT).show()
        else if(resultCode == RESULT_OK)
        {
            Toast.makeText(this, "modify something", Toast.LENGTH_SHORT).show()
            if (data != null) {
                this.questionTitle = data.getStringExtra("Key_title").toString()
                this.questionAnswerDescription = data.getStringExtra("Key_answerDescription").toString()
                this.questionNumber = data.getStringExtra("Key_number").toString()
                this.questionType = data.getStringExtra("Key_type").toString()
            }
        }
    }
    private fun init()
    {
        val optionNum = arrayOf("A", "B", "C", "D", "E", "F")
        val id = intent.getStringExtra("Key_id")
        val title = intent.getStringExtra("Key_title")
        val type = intent.getStringExtra("Key_type")
        val image = intent.getIntExtra("Key_image",0)
        val timeLimit = intent.getIntExtra("Key_timeLimit",0).toString() + "秒"
        val options = intent.getStringArrayListExtra("Key_options")
        val tag = intent.getStringArrayListExtra("Key_tag")
        val description = intent.getStringExtra("Key_description")
        val number = intent.getStringExtra("Key_number")
        val questionBank = intent.getStringExtra("Key_questionBank")
        val provider = intent.getStringExtra("Key_provider")
        val createdDate = intent.getStringExtra("Key_createdDate")
        val answerOption = intent.getStringArrayListExtra("Key_answerOptions")
        val answerDescription = intent.getStringExtra("Key_answerDescription")
        val tmpAnswerOptionInt : ArrayList<Int> = ArrayList()
        optionlist.clear()

        if (options != null) {
            optionListStr = options
            if(options.isNotEmpty())
                for(i in options.indices) {
                    val tmpOption = Option(optionNum[i], options[i])
                    optionlist.add(tmpOption)

                    //找到option中對的選項
                    if (answerOption != null) {
                        for(item in answerOption)
                            if(options[i]==item)
                                tmpAnswerOptionInt.add(i)
                    }
                }
                answerOptionInt = tmpAnswerOptionInt
        }
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
        this.time_limit = intent.getIntExtra("Key_timeLimit",0)


        questionBinding.QuestionImage.setImageResource(image)
        questionBinding.questionDescription.text = description
        questionBinding.timeLimit.text = timeLimit
        questionBinding.QuestionTitle.text = title

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
            tagTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
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
            tagTextView1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
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
            tagTextView2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag24, 0, 0, 0)
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
        val tmpAdapte: ListAdapter = questionBinding.QuestionOption.adapter
        val count = tmpAdapte.count
        val tmpList: ArrayList<String> = ArrayList()
        for (i in 0 until count)
        {
            val tmpOption  = tmpAdapte.getItem(i) as Option
            tmpList.add(tmpOption.optionContent)
        }
        optionListStr = tmpList
    }

    private fun backBtn(){
        val intent = Intent()
        val answerOptionListStr: ArrayList<String> = ArrayList()
        val timeLimitInt = time_limit
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
            for (i in answerOptionInt) {
                answerOptionListStr.add(optionListStr[i])
            }
            intent.putStringArrayListExtra("Key_answerOptions", answerOptionListStr)
            intent.putExtra("Key_timeLimit", timeLimitInt)
            intent.putExtra("Key_tagNum", this.questionTag.size)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun optionChange(position: Int, adapter: OptionAdapter) {
        val currentOption = optionlist[position]
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.edit_option, null)
        var status = false //isOptionChanged?
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
//                println("edit option text type is "+editOption.text::class.simpleName)
            if(status) {
                if(answerOptionIndices==-1) { //原先不是正確答案 後來是
                    if (this.questionType == "MultipleChoiceS")
                        answerOptionInt.clear()
                    answerOptionInt.add(position)
                }
            }
            else{
                if(answerOptionIndices!=-1) {  //原先是正確答案 後來不是
                    if(answerOptionInt.size > 1)
                        answerOptionInt.removeAt(answerOptionIndices)
                    else
                        AlertDialog.Builder(this).setTitle("至少要有一個正確選項!").setPositiveButton("我懂", null).show()
                }
            }

            adapter.setAnswerOptions(answerOptionInt)
        }
    }

    private fun selectTimeLimit(){
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.select_time_limit, null)
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
        val v:View =  layoutInflater.inflate(R.layout.edit_question_description, null)
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
        val v:View =  layoutInflater.inflate(R.layout.edit_question_tag, null)
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
}
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
//<TextView
//android:id="@+id/Question_tag1"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:padding="5dp"
//android:layout_marginHorizontal="3dp"
//android:gravity="center"
//android:text="成大"
//android:textSize="13dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>
//<TextView
//android:id="@+id/Question_tag2"
//android:layout_width="70dp"
//android:layout_height="wrap_content"
//android:background="@drawable/corner_radius_blue"
//android:layout_marginHorizontal="3dp"
//android:padding="5dp"
//android:gravity="center"
//android:text="考古題"
//android:textSize="13dp"
//android:textColor="@color/white"
//android:drawableLeft="@drawable/tag24"/>