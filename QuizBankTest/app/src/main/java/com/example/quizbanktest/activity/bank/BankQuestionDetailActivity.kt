package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnSetting: ImageButton
    // Question variable
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
    // variable
    private lateinit var newDescription: String


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        init()
        // TODO: deannotate it when data is full confirmed
//        if (questionImage == null) {
//            findViewById<ImageView>(R.id.question_image).visibility = View.INVISIBLE
//        }
        // set up question options
        setupOptions()

        tvDescription.setOnClickListener {
            val descriptionDialog = Dialog(this)
            descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
            descriptionDialog.show()

            val etDescription =
                descriptionDialog.findViewById<EditText>(R.id.et_question_description)
            etDescription.setText(newDescription)

            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // TODO: nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.e("BankQuestionDescriptionDialog", "Doing: $s")
                    newDescription = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                    Log.e("BankQuestionDescriptionDialog", "after: " + s.toString())

                }
            })
            descriptionDialog.setOnDismissListener {
                if (newDescription != "") {
                    Log.e("BankQuestionDescriptionDialog", "set new description")
                    etDescription.setText(newDescription)
                    tvDescription.text = newDescription
                }
            }
        }

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

    private fun setupOptions() {
        val tmpQuestionOptionsArrayList = ArrayList<String>()
        for (item in questionOptions) {
            tmpQuestionOptionsArrayList.add(item)
        }
        Log.e("BankQuestionDetailActivity", tmpQuestionOptionsArrayList.toString())

        val tmpAnswerOptionsArrayList = ArrayList<String>()
        for (item in answerOptions) {
            tmpAnswerOptionsArrayList.add(item)
        }

        val optionRecyclerView: RecyclerView = findViewById(R.id.optionRecyclerView)
        val optionAdapter =
            QuestionOptionsRecyclerViewAdapter(this, tmpQuestionOptionsArrayList, this)
        optionRecyclerView.adapter = optionAdapter
        optionRecyclerView.layoutManager = LinearLayoutManager(this)
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
        // View initialization
        tvTitle = findViewById(R.id.question_title)
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)
        btnSetting = findViewById(R.id.setting)

        tvTitle.text = questionTitle
        tvTitle.movementMethod = ScrollingMovementMethod()
        tvTitle.isSelected = true
        tvType.text = questionType
        tvDescription.movementMethod = ScrollingMovementMethod()
        tvDescription.text = questionDescription

        // Question variable initialization
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
        newDescription = questionDescription
        questionOptions = intent.getStringArrayListExtra("options")!!
        questionType = intent.getStringExtra("type").toString()
        answerOptions = intent.getStringArrayListExtra("answerOptions")!!
        answerDescription = intent.getStringExtra("answerDescription").toString()
        questionSource = intent.getStringExtra("source").toString()
        questionImage = intent.getStringArrayListExtra("image")!!
        questionTag = intent.getStringArrayListExtra("tag")!!
    }
}