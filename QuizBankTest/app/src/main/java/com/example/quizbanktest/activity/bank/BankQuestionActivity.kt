package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
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
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var backArrowBtn: ImageButton
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var questionAdapter: QuestionRecyclerViewAdapter

    // Question variable
    private var questionModels = ArrayList<QuestionModel>()

    // Variable
    private lateinit var bankTitle: String
    private lateinit var bankId: String
    private var toast: Toast? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)
//        setupNavigationView()
//        doubleCheckExit()

//        showProgressDialog("取得資料中")
        init()
//        hideProgressDialog()


        pullExit()
    }

    fun backToPreviousPage(view: View?) {
        finish()
    }

    private fun setupQuestionModel() {
        Log.e("BankQuestionActivity", "set up question model")
        questionRecyclerView = findViewById(R.id.questionRecyclerView)
        questionAdapter = QuestionRecyclerViewAdapter(this, questionModels, this)

        questionRecyclerView.adapter = questionAdapter
        questionRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        questionRecyclerView.layoutManager = LinearLayoutManager(this)
//        hideProgressDialog()

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(questionRecyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                buttons = listOf(deleteButton)
//                val markAsUnreadButton = markAsUnreadButton(position)
//                val archiveButton = archiveButton(position)
//                when (position) {
//                    1 -> buttons = listOf(deleteButton)
//                    2 -> buttons = listOf(deleteButton, markAsUnreadButton)
//                    3 -> buttons = listOf(deleteButton, markAsUnreadButton, archiveButton)
//                    else -> Unit
//                }
                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(questionRecyclerView)
    }

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    toast("Deleted item $position")
                }
            })
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

    fun init() {
        bankTitle = intent.getStringExtra("BankTitle").toString()
        bankId = intent.getStringExtra("BankId").toString()
        Log.e("BankQuestionActivity", "Bank title: $bankTitle, Bank id: $bankId")

        backArrowBtn = findViewById(R.id.btn_back_arrow)
        tvTitle = findViewById(R.id.title)

        tvTitle.text = bankTitle

        showProgressDialog("取得資料中")
        ConstantsQuestionFunction.getQuestion(this, bankId,
            onSuccess = { questionList ->
                Log.e("BankQuestionActivity", "get questionList success!!!")
                Log.e("BankQuestionActivity", "$questionList")
                for (item in questionList) {
                    val questionModel = QuestionModel(
                        item._id,
                        item.title,
                        item.number,
                        item.description,
                        item.options,
                        item.questionType,
                        item.bankType,
                        item.questionBank,
                        item.answerOptions,
                        item.answerDescription,
                        item.originateFrom,
                        item.createdDate,
                        item.image,
                        item.tag
                    )
                    questionModels.add(questionModel)
                }
                Log.e("BankQuestionActivity", "question model finish")
                setupQuestionModel()
                hideProgressDialog()
            },
            onFailure = { errorMessage ->
                showErrorSnackBar("網路連線狀況不好")
                hideProgressDialog()
            }
        )
    }

    override fun onItemClick(position: Int) {
        val QuestionDetailActivity = Intent(this, BankQuestionDetailActivity::class.java)

        QuestionDetailActivity.putExtra("id", questionModels[position]._id)
        QuestionDetailActivity.putExtra("title", questionModels[position].title)
        QuestionDetailActivity.putExtra("number", questionModels[position].number)
        QuestionDetailActivity.putExtra("description", questionModels[position].description)
        QuestionDetailActivity.putExtra("options", questionModels[position].options)
        QuestionDetailActivity.putExtra("type", questionModels[position].questionType)
        QuestionDetailActivity.putExtra("answerOptions", questionModels[position].answerOptions)
        QuestionDetailActivity.putExtra("answerDescription", questionModels[position].answerDescription)
        QuestionDetailActivity.putExtra("source", questionModels[position].originateFrom)
        QuestionDetailActivity.putExtra("createdDate", questionModels[position].createdDate)
        QuestionDetailActivity.putExtra("image", questionModels[position].image)
        QuestionDetailActivity.putExtra("tag", questionModels[position].tag)

//        Log.e("BankQuestionActivity", questionModels[position]._id.toString())

        startActivity(QuestionDetailActivity)
    }

}