package com.example.quizbanktest.activity.quiz

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.R
import com.example.quizbanktest.databinding.ActivitySingleQuestionSettingBinding
import com.example.quizbanktest.utils.Constants


class SingleQuestionSetting: AppCompatActivity()  {
    private lateinit var questionSetAttr: ActivitySingleQuestionSettingBinding
    private lateinit var questionTitle: String
    private lateinit var questionType: String
    private lateinit var questionNumber: String
    private var questionAnswerDescription: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionSetAttr = ActivitySingleQuestionSettingBinding.inflate(layoutInflater)
        setContentView(questionSetAttr.root)

        init()

        questionSetAttr.setAnswerDescription.setOnClickListener {
            answerDescriptionChange()
        }
        questionSetAttr.backBtn.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        questionSetAttr.saveBtn.setOnClickListener {
            val intent = Intent()
            val tmpTitleText = questionSetAttr.QuestionTitle.text
            val tmpAnsText = questionSetAttr.QuestionAnswerDescription.text
            val titleText = tmpTitleText.toString()
            val ansDesc = tmpAnsText.toString()
            intent.putExtra("Key_title", titleText)
            intent.putExtra("Key_answerDescription", ansDesc)
            intent.putExtra("Key_number", questionNumber)
            intent.putExtra("Key_type", questionType)
            setResult(RESULT_OK, intent)
            finish()
        }
        questionSetAttr.QuestionDelete.setOnClickListener {
            val intent = Intent()
            setResult(Constants.RESULT_DELETE, intent)
            finish()
        }
    }
    private fun init(){
        val title = intent.getStringExtra("Key_title")
        val answerDescription = intent.getStringExtra("Key_answerDescription")
        val number = intent.getStringExtra("Key_number")
        val type = intent.getStringExtra("Key_type")
        val questionBank = intent.getStringExtra("Key_questionBank")
        val provider = intent.getStringExtra("Key_provider")
        val createdDate = intent.getStringExtra("Key_createdDate")
        val typeTextView = TextView(this)

        if (number != null) {
            this.questionNumber = number
        }
        if (type != null) {
            this.questionType = type
        }
        if (answerDescription != null) {
            this.questionAnswerDescription = answerDescription
        }
        if (title != null) {
            this.questionTitle = title
        }


        if(type=="MultipleChoiceS" || type=="MultipleChoiceM") { //多選 單選
            val typeTextArr= arrayOf("單選", "多選")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeTextArr)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            questionSetAttr.QuestionType.adapter = adapter
            questionSetAttr.QuestionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0)
                        questionType = "MultipleChoiceS"
                    else if (position == 1)
                        questionType = "MultipleChoiceM"
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }else{   //填空 簡答
            val textSize1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics)
            val marginHorizontal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
            val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParam.marginStart = marginHorizontal
            typeTextView.text = if(type=="Filling") "填空" else if(type=="ShortAnswer") "簡答" else "是非"
            typeTextView.layoutParams = layoutParam
            typeTextView.gravity = Gravity.CENTER_VERTICAL
            typeTextView.textSize = textSize1
        }

        if(type=="MultipleChoiceS")
            questionSetAttr.QuestionType.setSelection(0)
        else if(type=="MultipleChoiceM")
            questionSetAttr.QuestionType.setSelection(1)
        else {
            questionSetAttr.QuestionTypeContainer.removeView(questionSetAttr.QuestionType)
            questionSetAttr.QuestionTypeContainer.addView(typeTextView)
        }

        questionSetAttr.QuestionBank.text = questionBank
        questionSetAttr.QuestionNumber.text = number
        questionSetAttr.QuestionProvider.text = provider
        questionSetAttr.QuestionTitle.setText(title)
        questionSetAttr.QuestionAnswerDescription.setText(answerDescription)
        questionSetAttr.QuestionCreateDate.text = createdDate
    }

    private fun answerDescriptionChange(){
        Log.d("in answerDes change", "single question setting")
        val builder = AlertDialog.Builder(this)
        val v:View =  layoutInflater.inflate(R.layout.dialog_edit_question_description, null)
        val editDescription: EditText = v.findViewById(R.id.edit_question_description)
        editDescription.setText(this.questionAnswerDescription)
        builder.setTitle("答案描述")
        builder.setView(v)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.setOnDismissListener {
            this.questionAnswerDescription = editDescription.text.toString()
            questionSetAttr.QuestionAnswerDescription.text = this.questionAnswerDescription
        }
    }
}

//<TextView
//android:layout_width="wrap_content"
//android:layout_height="wrap_content"
//android:layout_marginLeft="5dp"
//android:text="1"
//android:textSize="18dp"
//android:layout_gravity="center_vertical"/>