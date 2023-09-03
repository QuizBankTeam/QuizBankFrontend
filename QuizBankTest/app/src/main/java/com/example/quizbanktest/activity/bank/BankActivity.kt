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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
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
import java.time.LocalDate

class BankActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var searchView: SearchView
    private lateinit var menuButton: ImageButton
    private lateinit var bank_warning: TextView
    private lateinit var viewDialog: View
    private lateinit var etBankCreatedDate: EditText
    private lateinit var etBankMembers: EditText
    private lateinit var etBankSource: EditText
    private lateinit var bankRecyclerView: RecyclerView
    private lateinit var bankAdapter: BankRecyclerViewAdapter
    // Bank variable
    private var questionBankModels = ArrayList<QuestionBankModel>()
    // Variable
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private var toast: Toast? = null



    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        setupNavigationView()
        doubleCheckExit()

        init()
        setupBankModel()

        val groupBtn: ImageButton = findViewById(R.id.bank_group)
        groupBtn.setOnClickListener {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnAddBank = findViewById<ImageButton>(R.id.bank_add)
        btnAddBank.setOnClickListener {
            val addBankDialog = Dialog(this)
            addBankDialog.setContentView(R.layout.dialog_add_bank)
            addBankDialog.show()

            val btnBankSubmit = addBankDialog.findViewById<ImageButton>(R.id.btn_bank_submit)
            btnBankSubmit.setOnClickListener {
                // TODO id setting needs to be flexible
                val bankId = "52fde333-3eba-4140-a244-2b1aaf992a0e"
                val bankTitle =
                    addBankDialog.findViewById<EditText>(R.id.bank_title).text.toString()
                val bankType = "single"
                val bankCreatedDate = LocalDate.now().toString()
                val bankMembers: ArrayList<String> = arrayListOf()
                bankMembers.add("52fde333-3eba-4140-a244-2b1aaf992a0e")
                bankMembers.add("52fde333-3eba-4140-a244-2b1aaf992a0e")
                val bankSource = "52fde333-3eba-4140-a244-2b1aaf992a0e"
                val bankCreator = "none"
                val tempQuestionBankModel = QuestionBankModel(
                    bankId,
                    bankTitle,
                    bankType,
                    bankCreatedDate,
                    bankMembers,
                    bankSource,
                    bankCreator
                )
                showProgressDialog("新增中")
                ConstantsQuestionBankFunction.postQuestionBank(tempQuestionBankModel, this,
                    onSuccess = {
                        Toast.makeText(this, "add bank success", Toast.LENGTH_SHORT).show()
                        Log.d("addBankDialog", "add bank success")
                        addBankDialog.dismiss()
                        ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
                            onSuccess = { questionBanks ->
                                val intent = Intent(this@BankActivity, BankActivity::class.java)
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
        }
    }

    private fun setupBankModel() {

        bankRecyclerView = findViewById(R.id.bankRecyclerView)
        bankAdapter = BankRecyclerViewAdapter(this, questionBankModels, this)

        bankRecyclerView.adapter = bankAdapter
        bankRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        bankRecyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(bankRecyclerView) {
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

        itemTouchHelper.attachToRecyclerView(bankRecyclerView)
    }

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
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


    @SuppressLint("ClickableViewAccessibility")
    fun setPopupWindow(view: View?) {
        showProgressDialog("處理中")
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
            Log.d(
                getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms"
            )
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
            height = displayMetrics.heightPixels - 200
            width = displayMetrics.widthPixels - 200
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

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        myContentView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()     /* It will dismiss the popup window when tapped in it */
            return@setOnTouchListener true
        }
        hideProgressDialog()
    }

    fun init() {
        val bankId = ArrayList<String>()
        val bankTitle = ArrayList<String>()
        val bankType = ArrayList<String>()
        val bankCreatedDate = ArrayList<String>()
        val bankMembers = ArrayList<ArrayList<String>>()
        val bankSource = ArrayList<String>()
        val bankCreators = ArrayList<String>()

        for (item in ConstantsQuestionBankFunction.questionBankList) {
            bankId.add(item._id)
            bankTitle.add(item.title)
            bankType.add(item.questionBankType)
            bankCreatedDate.add(item.createdDate)
            bankMembers.add(item.members)
            bankSource.add(item.originateFrom)
            bankCreators.add(item.creator)
        }
        for (i in bankTitle.indices) {
            val questionBankModel = QuestionBankModel(
                bankId[i], bankTitle[i], bankType[i],
                bankCreatedDate[i], bankMembers[i], bankSource[i], bankCreators[i]
            )
            questionBankModels.add(questionBankModel)
        }
    }

    override fun onItemClick(position: Int) {
        val bankQuestionActivity = Intent(this, BankQuestionActivity::class.java)

        bankQuestionActivity.putExtra("BankTitle", questionBankModels[position].title)
        bankQuestionActivity.putExtra("BankId", questionBankModels[position]._id)
        Log.e("BankActivity", "start id: " + questionBankModels[position]._id + " bank")
        Log.e("BankActivity", "start bankQuestion activity")

        startActivity(bankQuestionActivity)
    }
}
