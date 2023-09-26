package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.adapters.bank.BankRecyclerViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.view.WrapLayout
import jp.wasabeef.blurry.Blurry
import java.time.LocalDate


class BankActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var searchView: SearchView
    private lateinit var menuButton: ImageButton
    private lateinit var btnGroup: ImageButton
    private lateinit var btnAddBank: ImageButton
    private lateinit var bank_warning: TextView
    private lateinit var viewDialog: View
    private lateinit var etBankCreatedDate: EditText
    private lateinit var etBankMembers: EditText
    private lateinit var etBankSource: EditText
    private lateinit var bankRecyclerView: RecyclerView
    private lateinit var bankAdapter: BankRecyclerViewAdapter
    private lateinit var bankLayout: LinearLayout

    // Bank variable
    private var questionBankModels = ArrayList<QuestionBankModel>()

    // Variable
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    private var toast: Toast? = null
    private lateinit var newBankTitle: String
    private lateinit var newBankType: String
    private lateinit var newBankDate: String
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

        btnAddBank.setOnClickListener { addBank() }
    }

    private fun setupBankModel() {

        bankRecyclerView = findViewById(R.id.bankRecyclerView)
        bankAdapter = BankRecyclerViewAdapter(this, this, questionBankModels, this)

        bankRecyclerView.adapter = bankAdapter
//        bankRecyclerView.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )
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

    private fun addBank() {
        val addBankDialog = Dialog(this)
        addBankDialog.setContentView(R.layout.dialog_add_bank)
        addBankDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addBankDialog.window?.setGravity(Gravity.CENTER)
        addBankDialog.show()

        val btnBankSubmit = addBankDialog.findViewById<TextView>(R.id.btn_bank_submit)
        btnBankSubmit.setOnClickListener {
            val bankId = Constants.userId
            val bankTitle =
                addBankDialog.findViewById<EditText>(R.id.bank_title).text.toString()
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

            if (bankTitle.isEmpty() || bankTitle == null) {
                showErrorSnackBar("名稱不可為空")
            } else {
                showProgressDialog("新增中")
                ConstantsQuestionBankFunction.postQuestionBank(tempQuestionBankModel, this,
                    onSuccess = {
                        Toast.makeText(this, "add bank success", Toast.LENGTH_SHORT).show()
                        Log.d("addBankDialog", "add bank success")
                        addBankDialog.dismiss()
                        bankAdapter.addItem(tempQuestionBankModel)
                        findViewById<ImageView>(R.id.img_empty).visibility = View.INVISIBLE
                        hideProgressDialog()
                    },
                    onFailure = {
                        Toast.makeText(this, "error type of data", Toast.LENGTH_SHORT).show()
                        Log.e("addBankDialog", "add bank failed")
                        addBankDialog.dismiss()
                        showErrorSnackBar("新增失敗")
                        hideProgressDialog()
                    }
                )
            }
        }
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
                    val deleteWarningDialog = Dialog(this@BankActivity)
                    deleteWarningDialog.setContentView(R.layout.dialog_delete_warning)
                    deleteWarningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    deleteWarningDialog.window?.setGravity(Gravity.CENTER)
                    deleteWarningDialog.show()

                    val btnConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_confirm)
                    val btnCancel = deleteWarningDialog.findViewById<TextView>(R.id.btn_cancel)

                    btnConfirm.setOnClickListener {
                        bankAdapter.deleteItem(position)
                        toast("Deleted item $position")
                        deleteWarningDialog.dismiss()
                    }
                    btnCancel.setOnClickListener {
                        deleteWarningDialog.dismiss()
                    }
                }
            })
    }

    private fun settingButton(position: Int): SwipeHelper.UnderlayButton {
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
        editBankDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editBankDialog.window?.setGravity(Gravity.CENTER)
        editBankDialog.show()

        val etBankTitle = editBankDialog.findViewById<EditText>(R.id.bank_title)
        val etBankType = editBankDialog.findViewById<EditText>(R.id.bank_type)
        val etBankDate = editBankDialog.findViewById<EditText>(R.id.bank_createdDate)
        val btnSubmit = editBankDialog.findViewById<TextView>(R.id.btn_submit)

        etBankTitle.setText(questionBankModels[position].title)
        etBankType.setText(questionBankModels[position].questionBankType)
        etBankDate.setText(questionBankModels[position].createdDate)

        etBankTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankTitle = s.toString()
                btnSubmit.visibility = View.VISIBLE
                isModified = true
            }
        })

        etBankType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankType = s.toString()
                btnSubmit.visibility = View.VISIBLE
                isModified = true
            }
        })

        etBankDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                newBankDate = s.toString()
                btnSubmit.visibility = View.VISIBLE
                isModified = true
            }
        })

        btnSubmit.setOnClickListener {
            if (isModified) {
                val data = QuestionBankModel(
                    questionBankModels[position]._id, newBankTitle,
                    newBankType, newBankDate, questionBankModels[position].members,
                    questionBankModels[position].originateFrom, questionBankModels[position].creator
                )
                Log.e("BankActivity", "new data = $data")
                bankAdapter.setItem(position, data)
                editBankDialog.dismiss()
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
        Log.e("BankActivity", "start init")
        if (ConstantsQuestionBankFunction.questionBankList != null) {
            if (ConstantsQuestionBankFunction.questionBankList.isEmpty()) {
                Log.e("BankActivity", "is empty")
                showEmptySnackBar("裡面沒有題庫喔~")
                findViewById<ImageView>(R.id.img_empty).visibility = View.VISIBLE
            } else {
                for (item in ConstantsQuestionBankFunction.questionBankList) {
                    val questionBankModel = QuestionBankModel(
                        item._id, item.title, item.questionBankType,
                        item.createdDate, item.members, item.originateFrom, item.creator
                    )
                    questionBankModels.add(questionBankModel)
                }
            }
        } else {
            showErrorSnackBar("Error, data is null")
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

    override fun switchBank(newBankPosition: Int) {
        //TODO
    }

    override fun settingCard(position: Int) {
        val settingBankDialog = Dialog(this)
        settingBankDialog.setContentView(R.layout.dialog_setting_panel)
        settingBankDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        settingBankDialog.window?.setGravity(Gravity.CENTER)
        settingBankDialog.findViewById<TextView>(R.id.tv_switch_position).visibility = View.GONE
        settingBankDialog.show()

    }

    override fun updateOption(position: Int, newOption: String) {
//        TODO("Not yet implemented")
    }

}
