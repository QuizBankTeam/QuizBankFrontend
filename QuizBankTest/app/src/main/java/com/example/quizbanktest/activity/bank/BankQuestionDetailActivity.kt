package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.adapters.bank.QuestionOptionsRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface


class BankQuestionDetailActivity : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnBackArrow: ImageButton

    private lateinit var questionId: String
    private lateinit var questionTitle: String
    private lateinit var questionNumber: String
    private lateinit var questionDescription: String
    private lateinit var questionOptions: ArrayList<String>
    private lateinit var questionType: String
    private lateinit var answerOptions: ArrayList<String>
    private lateinit var answerDescription: String
    private lateinit var questionSource: String
    private lateinit var questionImage: ArrayList<String>
    private lateinit var questionTag: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        init()

//        val questionId = intent.getStringExtra("id").toString()
//        val questionTitle = intent.getStringExtra("title").toString()
//        val questionNumber = intent.getStringExtra("number").toString()
////        val questionDescription = intent.getStringExtra("description").toString()
//        val questionDescription =
//            "This is a test string for testing the scroll function of TextView is usable or not.\n" +
//                    "The tested function are scrollbars in xml file and movementMethod in kotlin file.\n" +
//                    "Here is a testing article below:\n" +
//                    "The Collateral Repair Podcast aims to share the stories of refugees living in Amman, Jordan.\n" +
//                    "On a monthly basis, CRP invites you into an intimate space that will allow you to hear and understand refugees’ experiences in their own words.\n" +
//                    "Each episode features an interview with a member of one of Jordan’s refugee communities,\n" +
//                    "a supporting interview with a professional or employee at CRP, a Q&A in response to listeners’ questions,\n" +
//                    "and a quick update of developments that month at CRP."
//        val questionOptions = intent.getStringArrayListExtra("options")
//        val questionType = intent.getStringExtra("type").toString()
//        val answerOptions = intent.getStringArrayListExtra("answerOptions")
//        val answerDescription = intent.getStringExtra("answerDescription").toString()
//        val questionSource = intent.getStringExtra("source").toString()
//        val questionImage = intent.getStringArrayListExtra("image")
//        val questionTag = intent.getStringArrayListExtra("tag")

        Log.d("BankQuestionDetailActivity", questionDescription)

        tvTitle = findViewById(R.id.question_title)
        tvTitle.movementMethod = ScrollingMovementMethod()
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)
        tvDescription.movementMethod = ScrollingMovementMethod()

        tvTitle.text = questionTitle
        tvType.text = questionType
        tvDescription.text = questionDescription

        val tmpQuestionOptionsArrayList = ArrayList<String>()
        if (questionOptions != null) {
            for (item in questionOptions) {
                tmpQuestionOptionsArrayList.add(item)
            }
            Log.e("BankQuestionDetailActivity", tmpQuestionOptionsArrayList.toString())
        } else {
            Log.e("BankQuestionDetailActivity", "questionOptions is empty")
        }

        val tmpAnswerOptionsArrayList = ArrayList<String>()
        if (answerOptions != null) {
            for (item in answerOptions) {
                tmpAnswerOptionsArrayList.add(item)
            }
        } else {
            Log.e("BankQuestionDetailActivity", "answerOptions is empty")
        }
        // TODO deannotate it when data is full confirmed
//        if (questionImage == null) {
//            findViewById<ImageView>(R.id.question_image).visibility = View.INVISIBLE
//        }
        val optionRecyclerView : RecyclerView = findViewById(R.id.optionRecyclerView)
        val optionAdapter = QuestionOptionsRecyclerViewAdapter(this, tmpQuestionOptionsArrayList, this)
        optionRecyclerView.adapter = optionAdapter
        optionRecyclerView.layoutManager = LinearLayoutManager(this)

        setupOptions(tmpQuestionOptionsArrayList, tmpAnswerOptionsArrayList)

        tvDescription.setOnClickListener {
            val descriptionDialog = Dialog(this)
            descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
            descriptionDialog.show()

            val etDescription =
                descriptionDialog.findViewById<EditText>(R.id.et_question_description)
            etDescription.setText(questionDescription)

        }

        val btnSetting = findViewById<ImageButton>(R.id.setting)
        btnSetting.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    BankQuestionSettingActivity::class.java
                )
            )
        }

        pullExit()
    }

    private fun setupOptions(
        tmpQuestionOptionsArrayList: ArrayList<String>,
        tmpAnswerOptionsArrayList: ArrayList<String>
    ) {
//        val optionAdapter = QuestionOptionsRecyclerViewAdapter(this, tmpQuestionOptionsArrayList)
//        optionRecyclerView.adapter = optionAdapter
//        optionRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun pullExit() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                doubleBackToExit()
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onItemClick(position: Int) {
        Log.e("BankQuestionDetailActivity", "show option dialog")
        val optionDialog = Dialog(this)
        optionDialog.setContentView(R.layout.dialog_bank_question_option)
        optionDialog.show()

        val etOptionDescription =
            optionDialog.findViewById<EditText>(R.id.et_option_description)
        etOptionDescription.setText(questionOptions[position])
    }

    private fun init() {
        questionId = intent.getStringExtra("id").toString()
        questionTitle = intent.getStringExtra("title").toString()
        questionNumber = intent.getStringExtra("number").toString()
//        val questionDescription = intent.getStringExtra("description").toString()
        questionDescription =
            "This is a test string for testing the scroll function of TextView is usable or not.\n" +
                    "The tested function are scrollbars in xml file and movementMethod in kotlin file.\n" +
                    "Here is a testing article below:\n" +
                    "The Collateral Repair Podcast aims to share the stories of refugees living in Amman, Jordan.\n" +
                    "On a monthly basis, CRP invites you into an intimate space that will allow you to hear and understand refugees’ experiences in their own words.\n" +
                    "Each episode features an interview with a member of one of Jordan’s refugee communities,\n" +
                    "a supporting interview with a professional or employee at CRP, a Q&A in response to listeners’ questions,\n" +
                    "and a quick update of developments that month at CRP."
        questionOptions = intent.getStringArrayListExtra("options")!!
        questionType = intent.getStringExtra("type").toString()
        answerOptions = intent.getStringArrayListExtra("answerOptions")!!
        answerDescription = intent.getStringExtra("answerDescription").toString()
        questionSource = intent.getStringExtra("source").toString()
        questionImage = intent.getStringArrayListExtra("image")!!
        questionTag = intent.getStringArrayListExtra("tag")!!
    }
}