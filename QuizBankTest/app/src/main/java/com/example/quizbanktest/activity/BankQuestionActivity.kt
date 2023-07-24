package com.example.quizbanktest.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.BankRecyclerViewAdapter
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction

class BankQuestionActivity : AppCompatActivity() {

    private lateinit var tv_title: TextView
    private lateinit var backArrowBtn: ImageButton
    private var questionModels = ArrayList<QuestionBankModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)

        val name = intent.getStringExtra("NAME")
        tv_title = findViewById(R.id.title)
        tv_title.text = name

        backArrowBtn = findViewById(R.id.btn_back_arrow)

        val recyclerView : RecyclerView = findViewById(R.id.questionRecyclerView)
        setQuestionModels()
//        val adapter = BankRecyclerViewAdapter(this, questionModels)

//        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.question_to_bank_in, R.anim.question_to_bank_out)
    }

    private fun setQuestionModels() {
//        var bankID = ArrayList<String>()
//        var bankTitle = ArrayList<String>()
//        var bankType = ArrayList<String>()
//        var bankCreatedDate = ArrayList<String>()
//        var bankMembers = ArrayList<ArrayList<String>>()
//        var bankOriginateFrom = ArrayList<String>()
//        var bankCreators = ArrayList<String>()
//
//        Log.e("MainActivity", "ConstantsQuestionBankFunction.questionBankList")
//
//        if (ConstantsQuestionBankFunction.questionBankList != null) {
//            for (item in ConstantsQuestionBankFunction.questionBankList) {
//                bankID.add(item._id)
//                bankTitle.add(item.title)
//                bankType.add(item.questionBankType)
//                bankCreatedDate.add(item.createdDate)
//                bankMembers.add(item.members)
//                bankOriginateFrom.add(item.originateFrom)
//                bankCreators.add(item.creator)
//            }
//            for (i in bankTitle.indices) {
//                val questionBankModel = QuestionBankModel(bankID[i], bankTitle[i], bankType[i], bankCreatedDate[i],
//                    bankMembers[i], bankOriginateFrom[i], bankCreators[i])
//
//                questionBankModels.add(questionBankModel)
//            }
//        } else {
//            bank_warning = findViewById(R.id.bank_warning)
//            bank_warning.text = "這裡還沒有任何資料喔~"
//        }
    }
}