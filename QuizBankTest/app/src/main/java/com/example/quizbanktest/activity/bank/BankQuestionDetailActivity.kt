package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.group.GroupListActivity


class BankQuestionDetailActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnBackArrow: ImageButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        val questionId = intent.getStringExtra("id").toString()
        val questionTitle = intent.getStringExtra("title").toString()
        val questionNumber = intent.getStringExtra("number").toString()
//        val questionDescription = intent.getStringExtra("description").toString()
        val questionDescription =
            "This is a test string for testing the scroll function of TextView is usable or not.\n" +
                    "The tested function are scrollbars in xml file and movementMethod in kotlin file.\n" +
                    "Here is a testing article below:\n" +
                    "The Collateral Repair Podcast aims to share the stories of refugees living in Amman, Jordan.\n" +
                    "On a monthly basis, CRP invites you into an intimate space that will allow you to hear and understand refugees’ experiences in their own words.\n" +
                    "Each episode features an interview with a member of one of Jordan’s refugee communities,\n" +
                    "a supporting interview with a professional or employee at CRP, a Q&A in response to listeners’ questions,\n" +
                    "and a quick update of developments that month at CRP."
        val questionOptions = intent.getStringArrayListExtra("options")
        val questionType = intent.getStringExtra("type").toString()
        val answerOptions = intent.getStringArrayListExtra("answerOptions")
        val answerDescription = intent.getStringExtra("answerDescription").toString()
        val questionSource = intent.getStringExtra("source").toString()
        val questionImage = intent.getStringArrayListExtra("image")
        val questionTag = intent.getStringArrayListExtra("tag")

        Log.d("BankQuestionDetailActivity", questionDescription)

        tvTitle = findViewById(R.id.question_title)
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
        } else {
            Log.e("BankSingleAnswerQuestionActivity", "questionOptions is empty")
        }

        val tmpAnswerOptionsArrayList = ArrayList<String>()
        if (answerOptions != null) {
            for (item in answerOptions) {
                tmpAnswerOptionsArrayList.add(item)
            }
        } else {
            Log.e("BankSingleAnswerQuestionActivity", "answerOptions is empty")
        }
        // TODO deannotate it when data is full confirmed
//        if (questionImage == null) {
//            findViewById<ImageView>(R.id.question_image).visibility = View.INVISIBLE
//        }

//        recyclerView = findViewById(R.id.questionOptionsRecyclerView)
//        val adapter = QuestionOptionsRecyclerViewAdapter(this, tmpArrayList)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)

        setupOptions(tmpQuestionOptionsArrayList, tmpAnswerOptionsArrayList)

        val btnSetting = findViewById<ImageButton>(R.id.setting)
        btnSetting.setOnClickListener { startActivity(Intent(this, BankQuestionSettingActivity::class.java)) }

        pullExit()
    }

    private fun setupOptions(
        tmpQuestionOptionsArrayList: ArrayList<String>,
        tmpAnswerOptionsArrayList: ArrayList<String>
    ) {
//        val ll = findViewById<LinearLayout>(R.id.linearLayout_options)
//        val tv = TextView(this)
//        val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        tv.id = ""
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
}