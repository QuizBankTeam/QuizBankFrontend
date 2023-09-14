package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.adapters.bank.QuestionOptionsRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.google.android.material.imageview.ShapeableImageView


class BankQuestionDetailActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvAnswerDescription: TextView
    private lateinit var btnShowAnswer: TextView
    private lateinit var ivQuestionImage: ShapeableImageView
    private lateinit var optionRecyclerView: RecyclerView
    private lateinit var optionAdapter: QuestionOptionsRecyclerViewAdapter

    // Question variable
    private lateinit var questionId: String
    private lateinit var questionTitle: String
    private lateinit var questionNumber: String
    private lateinit var questionDescription: String
    private lateinit var questionOptions: ArrayList<String>
    private lateinit var questionType: String
    private lateinit var bankType: String
    private lateinit var bankId: String
    private lateinit var answerOptions: ArrayList<String>
    private lateinit var answerDescription: String
    private lateinit var questionSource: String
    private lateinit var createdDate: String
    private lateinit var questionImage: ArrayList<String>
    private lateinit var answerImage: ArrayList<String>
    private lateinit var questionTag: ArrayList<String>
    // variable
    private lateinit var newDescription: String
    private lateinit var newAnswerDescription: String
    private var isModified: Boolean = false
    private var isShowingAnswer: Boolean = true
    private var isShowingFillingAnswer: Boolean = false

    val list = mutableListOf<Int>()
    private var imageUrls = listOf(
        "https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.png",
        "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
        "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",
        "https://img.zcool.cn/community/01700557a7f42f0000018c1bd6eb23.jpg"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        init()
        setupOptions()


        btnShowAnswer.setOnClickListener{
            showAnswer()
        }

        tvDescription.setOnClickListener {
            val descriptionDialog = Dialog(this)
            descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
            descriptionDialog.show()

            val etDescription =
                descriptionDialog.findViewById<EditText>(R.id.et_question_description)
            etDescription.setText(newDescription)

            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newDescription = s.toString()
                    isModified = true
                }
            })

            descriptionDialog.setOnDismissListener {
                if (isModified) {
                    Log.e("BankQuestionDescriptionDialog", "set new description")
                    etDescription.setText(newDescription)
                    tvDescription.text = newDescription
                }

            }
        }

        tvAnswerDescription.setOnClickListener {
            val descriptionDialog = Dialog(this)
            descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
            descriptionDialog.show()

            val etDescription =
                descriptionDialog.findViewById<EditText>(R.id.et_question_description)
            etDescription.setText(newAnswerDescription)

            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newAnswerDescription = s.toString()
                    isModified = true
                }
            })

            descriptionDialog.setOnDismissListener {
                if (isModified) {
                    Log.e("BankQuestionDescriptionDialog", "set new description")
                    etDescription.setText(newAnswerDescription)
                    tvAnswerDescription.text = newAnswerDescription
                }

            }
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
        Log.e("BankQuestionDetailActivity", tmpAnswerOptionsArrayList.toString())
        optionRecyclerView = findViewById(R.id.optionRecyclerView)
        optionAdapter = QuestionOptionsRecyclerViewAdapter(this, questionType, tmpQuestionOptionsArrayList, tmpAnswerOptionsArrayList, this)
        optionRecyclerView.setHasFixedSize(true)

        when (questionType) {
            "TrueOrFalse" -> {
                optionRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            }
            "Filling" -> {

            }
            else -> {
                optionRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

        optionRecyclerView.adapter = optionAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAnswer() {
        if (isShowingAnswer) {
            btnShowAnswer.setBackgroundColor(Color.parseColor("#ff7575"))
            btnShowAnswer.text = "隱藏答案"
        }
        else {
            btnShowAnswer.setBackgroundColor(Color.parseColor("#c6fa73"))
            btnShowAnswer.text = "顯示答案"
        }
        isShowingAnswer = !isShowingAnswer

        if (questionType == "Filling") {
            if (!isShowingFillingAnswer) {
                tvAnswerDescription.visibility = View.VISIBLE
            }
            else {
                tvAnswerDescription.visibility = View.INVISIBLE
            }
            isShowingFillingAnswer  = !isShowingFillingAnswer

        } else {
            optionAdapter.showAnswer()
            optionAdapter.notifyDataSetChanged()
        }
    }

    private fun putQuestion() {
        Log.e("BankQuestionDetailActivity", "put question")
        if (isModified) {
            val putQuestionBody = QuestionModel(questionId, questionTitle, questionNumber, questionDescription, questionOptions, questionType, bankType, bankId, answerOptions, answerDescription, questionSource, createdDate, questionImage, answerImage, questionTag)
            ConstantsQuestionFunction.putQuestion(this, putQuestionBody,
                onSuccess = {
                    Log.e("BankQuestionDetailActivity", "upload success")
                },
                onFailure = { errorMessage ->
//                    showErrorSnackBar("網路連線狀況不好")
//                    hideProgressDialog()
                    Log.e("BankQuestionDetailActivity", "Error Message: $errorMessage" )
                }
            )
        }
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

    fun backToPreviousPage(view: View?) {
        putQuestion()
        this.finish()
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        putQuestion()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        putQuestion()
        finish()
    }

    override fun onItemClick(position: Int) {
        //TODO
    }

    private fun init() {
        // Question variable initialization
        questionId = intent.getStringExtra("id").toString()
        questionTitle = intent.getStringExtra("title").toString()
        questionNumber = intent.getStringExtra("number").toString()
        questionDescription = intent.getStringExtra("description").toString()
        val questionOptions = intent.getStringArrayListExtra("options")
        if (questionOptions != null) {
            this.questionOptions = questionOptions
        }
        questionType = intent.getStringExtra("type").toString()
        bankType = intent.getStringExtra("bankType").toString()
        bankId = intent.getStringExtra("bankId").toString()
        val answerOptions = intent.getStringArrayListExtra("answerOptions")
        if (answerOptions != null) {
            this.answerOptions = answerOptions
            answerOptions.add("roughly")
        }
        answerDescription = intent.getStringExtra("answerDescription").toString()
        questionSource = intent.getStringExtra("source").toString()
        createdDate = intent.getStringExtra("createdDate").toString()
        val questionImage = intent.getStringArrayListExtra("image")
        if (questionImage != null) {
            this.questionImage = questionImage
        }
        val answerImage = intent.getStringArrayListExtra("answerImage")
        if (answerImage != null) {
            this.answerImage = answerImage
        }
        val questionTag = intent.getStringArrayListExtra("tag")
        if (questionTag != null) {
            this.questionTag = questionTag
        }

        Log.e("BankQuestionDetailActivity", "image: $questionImage")

        // View initialization
        tvTitle = findViewById(R.id.question_title)
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)
        tvAnswerDescription = findViewById(R.id.answer_description)
        btnShowAnswer = findViewById(R.id.btn_show_answer)
        ivQuestionImage = findViewById(R.id.question_image)

        tvTitle.text = questionTitle
        tvTitle.movementMethod = ScrollingMovementMethod()
        tvTitle.isSelected = true
        tvType.text = questionType
        tvDescription.movementMethod = ScrollingMovementMethod()
        tvDescription.text = questionDescription
        tvAnswerDescription.movementMethod = ScrollingMovementMethod()
        tvAnswerDescription.text = answerDescription

        // Variable initialization
        newDescription = questionDescription
        newAnswerDescription = answerDescription

        if (questionImage!!.isEmpty()) {
            ivQuestionImage.visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.img_empty).visibility = View.VISIBLE
        }
    }
}