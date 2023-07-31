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

    private lateinit var tv_title: TextView
    private lateinit var backArrowBtn: ImageButton
    private var questionModel = ArrayList<QuestionModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)

        val bankTitle = intent.getStringExtra("BankTitle").toString()
        val bankID = intent.getStringExtra("BankID").toString()
        tv_title = findViewById(R.id.title)
        tv_title.text = bankTitle

        backArrowBtn = findViewById(R.id.btn_back_arrow)

        val recyclerView : RecyclerView = findViewById(R.id.questionRecyclerView)

        // TODO compare ID with Bank ID
        ConstantsQuestionFunction.getQuestion(this, bankID,
            onSuccess = { questions ->
//                setupQuestionModel()
            },
            onFailure = { errorMessage ->
                Toast.makeText(this,"get questions error", Toast.LENGTH_SHORT).show()
            }
        )

        val adapter = QuestionRecyclerViewAdapter(this, questionModel, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun backToPreviousPage(view: View?) {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.question_to_bank_in, R.anim.question_to_bank_out)
    }

    private fun setupQuestionModel() {
//        var bankID = ArrayList<String>()
//        var bankTitle = ArrayList<String>()
//        var bankType = ArrayList<String>()
//        var bankCreatedDate = ArrayList<String>()
//        var bankMembers = ArrayList<ArrayList<String>>()
//        var bankOriginateFrom = ArrayList<String>()
//        var bankCreators = ArrayList<String>()
//
        Log.i("BankQuestionActivity", "set question model")
//
//        if (ConstantsQuestionBankFunction.questionList != null) {
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

    override fun onItemClick(position: Int) {
//        val bankQuestionActivity = Intent(this, BankQuestionActivity:: class.java)
//
//        bankQuestionActivity.putExtra("BankTitle", questionBankModels[position].title)
//        bankQuestionActivity.putExtra("BankID", questionBankModels[position]._id)
//        Log.e("BankActivity", "start bankQuestion activity")
//
//        startActivity(bankQuestionActivity)
//        overridePendingTransition(R.anim.bank_to_question_out, R.anim.bank_to_question_in);
    }
}