package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.adapters.bank.BankRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsRecommend
import com.example.quizbanktest.utils.ConstantsWrong

import com.example.quizbanktest.view.WrapLayout
import jp.wasabeef.blurry.Blurry

class BankActivity : BaseActivity(), RecyclerViewInterface {
    private lateinit var searchView: SearchView
    private lateinit var menuButton: ImageButton
    private lateinit var bank_warning: TextView
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private var questionBankModels = ArrayList<QuestionBankModel>()

    private lateinit var viewDialog: View
    private lateinit var etBankCreatedDate: EditText
    private lateinit var etBankMembers: EditText
    private lateinit var etBankSource: EditText


    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
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

        val groupBtn : ImageButton = findViewById(R.id.bank_group)
        groupBtn.setOnClickListener {
            val intent = Intent(this,GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnAddBank = findViewById<ImageButton>(R.id.bank_add)
        btnAddBank.setOnClickListener {
            val addBankDialog = Dialog(this)
            addBankDialog.setContentView(R.layout.dialog_add_bank)
            addBankDialog.show()

            val etBankTitle = addBankDialog.findViewById<EditText>(R.id.bank_title)
            val etBankType = addBankDialog.findViewById<EditText>(R.id.bank_type)
            val etBankMembers = addBankDialog.findViewById<EditText>(R.id.bank_members)
//            val etBankSource = addBankDialog.findViewById<EditText>(R.id.bank_originatedFrom)

            val bankId = "52fde333-3eba-4140-a244-2b1aaf992a0e"
            val bankTitle = "test bank"
            val bankType = "single"
            // TODO Decide the type of variable below (EditText? TextView? ...)
            val bankCreatedDate = "2023-6-18"
            val bankMembers : ArrayList<String> = arrayListOf()
            bankMembers.add("52fde333-3eba-4140-a244-2b1aaf992a0e")
            bankMembers.add("52fde333-3eba-4140-a244-2b1aaf992a0e")
            val bankSource = "52fde333-3eba-4140-a244-2b1aaf992a0e"
            val bankCreator = "none"

            val btnBankSubmit = addBankDialog.findViewById<ImageButton>(R.id.btn_bank_submit)
            btnBankSubmit.setOnClickListener {
                val tempQuestionBankModel = QuestionBankModel(bankId, bankTitle, bankType, bankCreatedDate, bankMembers, bankSource, bankCreator)
                showProgressDialog("新增中")
                ConstantsQuestionBankFunction.postQuestionBank(tempQuestionBankModel, this,
                    onSuccess = {
                        Toast.makeText(this, "add bank success", Toast.LENGTH_SHORT).show()
                        Log.d("addBankDialog", "add bank success")
                        addBankDialog.dismiss()
                        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
                            onSuccess = { questionBanks ->
                                val intent = Intent(this@BankActivity,BankActivity::class.java)
                                startActivity(intent)
                                hideProgressDialog()
                                finish()
                            },
                            onFailure = { errorMessage ->
                                hideProgressDialog()
                            }
                        )
                    },
                    onFailure = {
                        Toast.makeText(this, "error type of data", Toast.LENGTH_SHORT).show()
                        Log.e("addBankDialog", "add bank failed")
                        addBankDialog.dismiss()
                    }
                )
            }

//            val btnAddBankMember = addBankDialog.findViewById<ImageButton>(R.id.btn_add_bank_member)
//            btnAddBankMember.setOnClickListener {
//                val ll = addBankDialog.findViewById<LinearLayout>(R.id.layout_bank_members)
//                val et = EditText(this)
//                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                et.layoutParams = p
//                et.hint = "enter members"
//                ll.addView(et)
////                et.requestFocus()
//            }
        }
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
            Log.e("creators of banks",bankCreators[i])
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

        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = myContentView.measuredHeight

        // popup window set up
        val popupWindow = PopupWindow(this).apply {
            contentView = myContentView
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

        popupWindow.setOnDismissListener {
            if (blurred) {
                blurred = false
                Blurry.delete(findViewById(R.id.content))
            }
        }

        // automatically newline tags view
        wrapLayout = myContentView.findViewById(R.id.clip_layout)
        val strs = arrayOf(
            "作業系統",
            "離散數學",
            "線性代數",
            "資料結構",
            "演算法",
            "計算機組織",
            "python",
            "java"
        )
        for (element in strs) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = element
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
    }

}
