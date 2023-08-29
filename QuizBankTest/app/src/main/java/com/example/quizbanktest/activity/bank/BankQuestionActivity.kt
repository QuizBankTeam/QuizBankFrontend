package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.adapters.bank.QuestionRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import kotlin.math.sin

class BankQuestionActivity : BaseActivity(), RecyclerViewInterface {

    private lateinit var tvTitle: TextView
    private lateinit var backArrowBtn: ImageButton
    private var questionModels = ArrayList<QuestionModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)

        val bankTitle = intent.getStringExtra("BankTitle").toString()
        val bankId = intent.getStringExtra("BankID").toString()
        tvTitle = findViewById(R.id.title)
        tvTitle.text = bankTitle

        backArrowBtn = findViewById(R.id.btn_back_arrow)

        val recyclerView : RecyclerView = findViewById(R.id.questionRecyclerView)

        showProgressDialog("處理中")

        // TODO compare ID with Bank ID
        ConstantsQuestionFunction.getQuestion(this, bankId,
            onSuccess = { questions ->
                setupQuestionModel()
                Log.d("BankQuestionActivity", "set question success!")
                val adapter = QuestionRecyclerViewAdapter(this, questionModels, this)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                Log.d("BankQuestionActivity", "RecyclerView setting finished")
                hideProgressDialog()
            },
            onFailure = { errorMessage ->
                showErrorSnackBar("網路連線狀況不好")
                hideProgressDialog()
            }
        )

        pullExit()

    }

    fun backToPreviousPage(view: View?) {
        finish()
    }

    private fun setupQuestionModel() {
        val questionId = ArrayList<String>()
        val questionTitle = ArrayList<String>()
        val questionNumber = ArrayList<String>()
        val questionDescription = ArrayList<String>()
        val questionOptions = ArrayList<ArrayList<String>>()
        val questionType = ArrayList<String>()
        val bankType = ArrayList<String>()
        val questionBankId = ArrayList<String>()
        val answerOptions = ArrayList<ArrayList<String>>()
        val answerDescription = ArrayList<String>()
        val questionSource = ArrayList<String>()
        val questionCreatedDate = ArrayList<String>()
        val questionImage = ArrayList<ArrayList<String>>()
        val tag = ArrayList<ArrayList<String>>()

        Log.i("BankQuestionActivity", "Set question model")

        for (item in ConstantsQuestionFunction.questionList) {
            questionId.add(item._id!!)
            questionTitle.add(item.title!!)
            questionNumber.add(item.number!!)
            questionDescription.add(item.description)
            questionOptions.add(item.options!!)
            questionType.add(item.bankType!!)
            bankType.add(item.bankType!!)
            questionBankId.add(item.questionBank!!)
            answerOptions.add(item.answerOptions!!)
            answerDescription.add(item.answerDescription!!)
//            questionSource.add(item.originateFrom)
            questionSource.add("test")
            questionCreatedDate.add(item.createdDate)
            questionImage.add(item.image!!)
            tag.add(item.tag!!)
        }
        for (i in questionTitle.indices) {
            val questionModel = QuestionModel(questionId[i], questionTitle[i], questionNumber[i],
                questionDescription[i], questionOptions[i], questionType[i], bankType[i],
                questionBankId[i], answerOptions[i], answerDescription[i], questionSource[i],
                questionCreatedDate[i], questionImage[i], tag[i])

            questionModels.add(questionModel)
        }
    }

    override fun onItemClick(position: Int) {
        val singleAnswerQuestionActivity = Intent(this, BankQuestionDetailActivity:: class.java)

        singleAnswerQuestionActivity.putExtra("id", questionModels[position]._id)
        singleAnswerQuestionActivity.putExtra("title", questionModels[position].title)
        singleAnswerQuestionActivity.putExtra("number", questionModels[position].number)
        singleAnswerQuestionActivity.putExtra("description", questionModels[position].description)
        singleAnswerQuestionActivity.putExtra("options", questionModels[position].options)
        singleAnswerQuestionActivity.putExtra("type", questionModels[position].questionType)
        singleAnswerQuestionActivity.putExtra("answerOptions", questionModels[position].answerOptions)
        singleAnswerQuestionActivity.putExtra("answerDescription", questionModels[position].answerDescription)
        singleAnswerQuestionActivity.putExtra("source", questionModels[position].originateFrom)
        singleAnswerQuestionActivity.putExtra("createdDate", questionModels[position].createdDate)
        singleAnswerQuestionActivity.putExtra("image", questionModels[position].image)
        singleAnswerQuestionActivity.putExtra("tag", questionModels[position].tag)

        Log.e("BankQuestionActivity", questionModels[position]._id.toString())

        startActivity(singleAnswerQuestionActivity)
    }

    override fun getAnswerOptionPosition(position: Int) {
        // TODO
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun pullExit(){
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