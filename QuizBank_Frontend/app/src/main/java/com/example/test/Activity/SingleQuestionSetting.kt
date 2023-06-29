package com.example.test.Activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.SingleQuestionSettingBinding


class SingleQuestionSetting: AppCompatActivity()  {
    private lateinit var questionSetAttr: SingleQuestionSettingBinding
    private lateinit var questionTitle: String
    private lateinit var questionType: String
    private lateinit var questionNumber: String
    private lateinit var questionAnswerDescription: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionSetAttr = SingleQuestionSettingBinding.inflate(layoutInflater)
        setContentView(questionSetAttr.root)

        init()

        questionSetAttr.backBtn.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        questionSetAttr.saveBtn.setOnClickListener {
            val intent = Intent()
            val titleText = questionSetAttr.QuestionTitle.text.toString()
            val ansDesc = questionSetAttr.QuestionAnswerDescription.text.toString()
            intent.putExtra("Key_title", titleText)
            intent.putExtra("Key_answerDescription", ansDesc)
            intent.putExtra("Key_number", questionNumber)
            intent.putExtra("Key_type", questionType)
            setResult(RESULT_OK, intent)
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
        val typeTextArr= arrayOf("單選", "多選")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, typeTextArr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSetAttr.QuestionType.adapter = adapter

        if (number != null) {
            this.questionNumber = number
        }
        if(type=="MultipleChoiceS")
            questionSetAttr.QuestionType.setSelection(0)
        else if(type=="MultipleChoiceM")
            questionSetAttr.QuestionType.setSelection(1)
        questionSetAttr.QuestionType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position==0)
                    questionType = "MultipleChoiceS"
                else if(position==1)
                    questionType = "MultipleChoiceM"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        questionSetAttr.QuestionBank.text = questionBank
        questionSetAttr.QuestionNumber.text = number
        questionSetAttr.QuestionProvider.text = provider
        questionSetAttr.QuestionTitle.setText(title)
        questionSetAttr.QuestionAnswerDescription.setText(answerDescription)
        questionSetAttr.QuestionCreateDate.text = createdDate
    }
}