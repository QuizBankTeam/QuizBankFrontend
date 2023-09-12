package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsRecommend

import com.example.quizbanktest.view.WrapLayout
import jp.wasabeef.blurry.Blurry
import org.w3c.dom.Text
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
    private lateinit var btnGroup: ImageButton
    private lateinit var btnAddBank: ImageButton

    // Bank variable
    private var questionBankModels = ArrayList<QuestionBankModel>()

    // Variable
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private var toast: Toast? = null
    private lateinit var newBankTitle: String
    private lateinit var newBankType: String
    private lateinit var newBankDate: String
    private lateinit var newBankSource: String
    private lateinit var newBankMembers: ArrayList<String>
    private lateinit var newBankCreator: String
    private var isModified: Boolean = false


    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        setupNavigationView()
        doubleCheckExit()

        init()
        setupBankModel()

        btnGroup.setOnClickListener {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAddBank.setOnClickListener {
            val addBankDialog = Dialog(this)
            addBankDialog.setContentView(R.layout.dialog_add_bank)
            addBankDialog.show()

            val btnBankSubmit = addBankDialog.findViewById<ImageButton>(R.id.btn_bank_submit)
            btnBankSubmit.setOnClickListener {
                // TODO id setting needs to be flexible
                val bankId = Constants.userId
                val bankTitle = addBankDialog.findViewById<EditText>(R.id.bank_title).text.toString()
                val bankType = "single"
                val bankCreatedDate = LocalDate.now().toString()
                val bankMembers: ArrayList<String> = arrayListOf()
                bankMembers.add(Constants.userId)
                val bankSource = Constants.userId
                val bankCreator = Constants.userId
                val tempQuestionBankModel = QuestionBankModel(
                    bankId, bankTitle, bankType, bankCreatedDate,
                    bankMembers, bankSource, bankCreator
                )
                showProgressDialog("新增中")
                ConstantsQuestionBankFunction.postQuestionBank(tempQuestionBankModel, this,
                    onSuccess = {
                        Toast.makeText(this, "add bank success", Toast.LENGTH_SHORT).show()
                        Log.d("addBankDialog", "add bank success")
                        addBankDialog.dismiss()
                        bankAdapter.addItem(tempQuestionBankModel)
                        hideProgressDialog()
                    },
                    onFailure = {
                        Toast.makeText(this, "error type of data", Toast.LENGTH_SHORT).show()
                        Log.e("addBankDialog", "add bank failed")
                        addBankDialog.dismiss()
                        hideProgressDialog()
                    }
                )
            }
        }
    }

    private fun setupBankModel() {

        bankRecyclerView = findViewById(R.id.bankRecyclerView)
        bankAdapter = BankRecyclerViewAdapter(this, this, questionBankModels, this)

        bankRecyclerView.adapter = bankAdapter
        bankRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        bankRecyclerView.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(bankRecyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                val settingButton = settingButton(position)
                buttons = listOf(deleteButton, settingButton)
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

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "刪除",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    bankAdapter.deleteItem(position)
                    toast("Deleted item $position")
                }
            })
    }

    private fun settingButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            this,
            "編輯",
            14.0f,
            android.R.color.holo_green_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    toast("Setting item $position")
                    editBank(position)
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun editBank(position: Int) {
        val editBankDialog = Dialog(this)
        editBankDialog.setContentView(R.layout.dialog_bank_card)
        editBankDialog.show()

        val etBankTitle = editBankDialog.findViewById<EditText>(R.id.bank_title)
        val etBankType = editBankDialog.findViewById<EditText>(R.id.bank_type)
        val etBankDate = editBankDialog.findViewById<EditText>(R.id.bank_createdDate)
        val etBankMember = editBankDialog.findViewById<EditText>(R.id.bank_members)
        val etBankSource = editBankDialog.findViewById<EditText>(R.id.bank_from)
        val etBankCreator = editBankDialog.findViewById<EditText>(R.id.bank_creator)

        newBankTitle = questionBankModels[position].title
        newBankType = questionBankModels[position].questionBankType
        newBankDate = questionBankModels[position].createdDate
        newBankMembers = questionBankModels[position].members
        newBankSource = questionBankModels[position].originateFrom
        newBankCreator = questionBankModels[position].creator

        etBankTitle.setText(newBankTitle)
        etBankType.setText(newBankType)
        etBankDate.setText(newBankDate)
        etBankMember.setText("Members: ${Constants.username}")
        etBankSource.setText("From: ${Constants.username}")
        etBankCreator.setText("Creator: ${Constants.username}")

        etBankTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankTitle = s.toString()
                isModified = true
            }
        })

        etBankType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankType = s.toString()
                isModified = true
            }
        })

        etBankDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankDate = s.toString()
                isModified = true
            }
        })

        editBankDialog.setOnDismissListener {
            if (isModified) {
                val data = QuestionBankModel(questionBankModels[position]._id, newBankTitle,
                    newBankType, newBankDate, questionBankModels[position].members,
                    newBankSource, newBankCreator)
                Log.e("BankActivity", "newdata = \n$data")
                bankAdapter.setItem(position, data)
            }

        }

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
        for (item in ConstantsQuestionBankFunction.questionBankList) {
            val questionBankModel = QuestionBankModel(
                item._id, item.title, item.questionBankType,
                item.createdDate, item.members, item.originateFrom, item.creator
            )
            questionBankModels.add(questionBankModel)
        }

        btnGroup = findViewById(R.id.bank_group)
        btnAddBank = findViewById(R.id.bank_add)
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
