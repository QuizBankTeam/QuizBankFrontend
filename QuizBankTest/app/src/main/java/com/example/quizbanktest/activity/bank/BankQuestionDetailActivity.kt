package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
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
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.adapters.bank.QuestionOptionsRecyclerViewAdapter
import com.example.quizbanktest.adapters.bank.QuestionRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import org.w3c.dom.Text


class BankQuestionDetailActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnSetting: ImageButton
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
    private lateinit var questionTag: ArrayList<String>
    // variable
    private lateinit var newDescription: String
    private lateinit var oldDescription: String
    private var isModified: Boolean = false


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

        findViewById<TextView>(R.id.btn_show_answer).setOnClickListener{
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    newDescription = s.toString()
                    isModified = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            descriptionDialog.setOnDismissListener {
                if (newDescription != "") {
                    Log.e("BankQuestionDescriptionDialog", "set new description")
                    etDescription.setText(newDescription)
                    tvDescription.text = newDescription
                }

            }
        }

        btnSetting.setOnClickListener { startActivity(Intent(this, BankQuestionSettingActivity::class.java)) }

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
        optionAdapter = QuestionOptionsRecyclerViewAdapter(this, tmpQuestionOptionsArrayList, tmpAnswerOptionsArrayList, this)
        optionRecyclerView.setHasFixedSize(true)

        when (questionType) {
            "true-false" -> {
                optionRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            }
            "cloze" -> {

            }
            else -> {
                optionRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

        optionRecyclerView.adapter = optionAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAnswer() {
        optionAdapter.showAnswer()
        optionAdapter.notifyDataSetChanged()
    }

    private fun putQuestion() {
        if (isModified) {
            val putQuestionBody = QuestionModel(questionId, questionTitle, questionNumber, questionDescription, questionOptions, questionType, bankType, bankId, answerOptions, answerDescription, questionSource, createdDate, questionImage, questionTag)
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
        questionOptions = intent.getStringArrayListExtra("options")!!
        questionType = intent.getStringExtra("type").toString()
        bankType = intent.getStringExtra("bankType").toString()
        bankId = intent.getStringExtra("bankId").toString()
        answerOptions = intent.getStringArrayListExtra("answerOptions")!!
        answerOptions.add("roughly")
        answerDescription = intent.getStringExtra("answerDescription").toString()
        questionSource = intent.getStringExtra("source").toString()
        createdDate = intent.getStringExtra("createdDate").toString()
        questionImage = intent.getStringArrayListExtra("image")!!
        questionTag = intent.getStringArrayListExtra("tag")!!

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

        // Variable initialization
        newDescription = questionDescription
    }
}
