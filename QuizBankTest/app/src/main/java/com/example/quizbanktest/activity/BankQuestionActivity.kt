package com.example.quizbanktest.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.BankRecyclerViewAdapter
import com.example.quizbanktest.adapters.QuestionRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.example.quizbanktest.utils.ConstantsRecommend
import com.example.quizbanktest.utils.ConstantsWrong

class BankQuestionActivity : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var tvTitle: TextView
    private lateinit var backArrowBtn: ImageButton
    private var questionModels = ArrayList<QuestionModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)

        val bankTitle = intent.getStringExtra("BankTitle").toString()
        val bankId = intent.getStringExtra("BankId").toString()
        tvTitle = findViewById(R.id.title)
        tvTitle.text = bankTitle

        backArrowBtn = findViewById(R.id.btn_back_arrow)

        val recyclerView : RecyclerView = findViewById(R.id.questionRecyclerView)

        // TODO compare ID with Bank ID
        ConstantsQuestionFunction.getQuestion(this, bankId,
            onSuccess = { questions ->
                setupQuestionModel()
                Log.d("BankQuestionActivity", "set question success!")
                val adapter = QuestionRecyclerViewAdapter(this, questionModels, this)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
                Log.d("BankQuestionActivity", "RecyclerView setting finished")
            },
            onFailure = { errorMessage ->
                Toast.makeText(this,"get questions error", Toast.LENGTH_SHORT).show()
            }
        )

    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.question_to_bank_in, R.anim.question_to_bank_out)
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
        val questionActivity = Intent(this, QuestionActivity:: class.java)

        questionActivity.putExtra("QuestionTitle", questionModels[position].title)
        questionActivity.putExtra("QuestionId", questionModels[position]._id)
        Log.e("BankQuestionActivity", "start question activity")

        startActivity(questionActivity)
        overridePendingTransition(R.anim.bank_to_question_out, R.anim.bank_to_question_in);
    }
}