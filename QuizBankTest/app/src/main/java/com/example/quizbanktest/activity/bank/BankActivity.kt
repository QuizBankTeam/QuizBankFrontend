package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.adapters.bank.BankRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction

import com.example.quizbanktest.view.WrapLayout
import jp.wasabeef.blurry.Blurry

class BankActivity : BaseActivity(), RecyclerViewInterface {
    lateinit var searchView: SearchView
    lateinit var menuButton: ImageButton
    lateinit var bank_warning: TextView
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private var questionBankModels = ArrayList<QuestionBankModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)


        setupNavigationView()
        doubleCheckExit()
        val recyclerView : RecyclerView = findViewById(R.id.bankRecyclerView)
        setupBankModel()
        val adapter = BankRecyclerViewAdapter(this, questionBankModels, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun setupBankModel() {
        val bankID = ArrayList<String>()
        val bankTitle = ArrayList<String>()
        val bankType = ArrayList<String>()
        val bankCreatedDate = ArrayList<String>()
        val bankMembers = ArrayList<ArrayList<String>>()
        val bankSource = ArrayList<String>()
        val bankCreators = ArrayList<String>()

        Log.e("BankActivity", "ConstantsQuestionBankFunction.questionBankList")

        for (item in ConstantsQuestionBankFunction.questionBankList) {
            bankID.add(item._id)
            bankTitle.add(item.title)
            bankType.add(item.questionBankType)
            bankCreatedDate.add(item.createdDate)
            bankMembers.add(item.members)
            bankSource.add(item.originateFrom)
            bankCreators.add(item.creator)
        }
        for (i in bankTitle.indices) {
            val questionBankModel = QuestionBankModel(bankID[i], bankTitle[i], bankType[i],
                bankCreatedDate[i], bankMembers[i], bankSource[i], bankCreators[i])

            questionBankModels.add(questionBankModel)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun setPopupWindow(view: View?) {

        if (blurred) {
            blurred = false
            Blurry.delete(findViewById(R.id.content))
        } else {
            blurred = true
            val startMs = System.currentTimeMillis()
            Blurry.with(this@BankActivity)
                .radius(25)
                .sampling(2)
                .async()
                .animate(200)
                .onto(findViewById<View>(R.id.content) as ViewGroup)
            Log.d(getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

//        blurred = !blurred
//        return true

        val popupInflater = getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = myContentView.measuredHeight

        // popup window set up
        val popupWindow = PopupWindow(this).apply {
            contentView = myContentView
//            width = ViewGroup.LayoutParams.MATCH_PARENT
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            height = displayMetrics.heightPixels-200
            width = displayMetrics.widthPixels-200

            animationStyle = R.style.PopupAnimation
            isFocusable = true
            isOutsideTouchable = false
            isClippingEnabled = false
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            if (blurred) {
                blurred = false
                Blurry.delete(findViewById(R.id.content))
            }

        })

        // automatically newline tags view
        wrapLayout = myContentView.findViewById(R.id.clip_layout)
        val strs = arrayOf(
            "奔跑吧兄弟",
            "running man",
            "笑傲江湖",
            "快樂大本營",
            "維多利亞的秘密",
            "非誠勿擾",
            "康熙來了",
            "123"
        )
        for (i in 0 until strs.size) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = strs[i]
            wrapLayout!!.addView(itemLayout)
        }

        popupWindow.showAtLocation(view, Gravity.CENTER,     0, 0)
        myContentView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()     /* It will dismiss the popup window when tapped in it */
            return@setOnTouchListener true
        }

    }
    override fun onItemClick(position: Int) {
        val bankQuestionActivity = Intent(this, BankQuestionActivity:: class.java)

        bankQuestionActivity.putExtra("BankTitle", questionBankModels[position].title)
        bankQuestionActivity.putExtra("BankID", questionBankModels[position]._id)
        Log.e("BankActivity", "start bankQuestion activity")

        startActivity(bankQuestionActivity)
//        overridePendingTransition(R.anim.bank_to_question_out, R.anim.bank_to_question_in);
    }
}
