package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
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
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate


class BankActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var searchView: SearchView
    private lateinit var btnSort: ImageButton
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
    private lateinit var newBankTitle: String
    private var wrapLayout: WrapLayout? = null
    private var isModified: Boolean = false
    private var isDescending: Boolean = true
    private val tagList: ArrayList<String> = ArrayList()
    private val backGroundArray: ArrayList<String> = arrayListOf(
        "#E6CAFF", "#FFB5B5", "#CECEFF", "#ACD6FF", "#FFF0AC", "#FFCBB3"
    )


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

        btnSort.setOnClickListener { setSortDialog() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                /**調用RecyclerView內的Filter方法 */
                bankAdapter.getFilter().filter(newText)
                return false
            }
        })
    }

    private fun setupBankModel() {

        bankRecyclerView = findViewById(R.id.bankRecyclerView)
        bankAdapter = BankRecyclerViewAdapter(this, this, questionBankModels, this)

        bankRecyclerView.adapter = bankAdapter
        bankRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSortDialog() {
        val sortDialog = Dialog(this)
        sortDialog.setContentView(R.layout.dialog_sort_tags)
        sortDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.window?.setGravity(Gravity.CENTER)
        sortDialog.show()

        /** views init */
        val btnSortByName = sortDialog.findViewById<TextView>(R.id.tv_sortByName)
        val btnSortByDate = sortDialog.findViewById<TextView>(R.id.tv_sortByDate)

        /*
        /** tags init */
        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wrapLayout = sortDialog.findViewById(R.id.clip_layout)
        val strs = arrayOf(
            "作業系統", "離散數學", "線性代數", "資料結構",
            "演算法", "計算機組織", "python", "java"
        )
        Log.e("BankQuestionActivity", "Here are the all tags below:\n$tagList")
        for (item in strs) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val tagName = itemLayout.findViewById<View>(R.id.name) as TextView
            tagName.text = item
            tagName.setBackgroundColor(Color.parseColor(backGroundArray[(0..5).random()]))
            wrapLayout!!.addView(itemLayout)
        }*/

        /** Listener area */
        btnSortByName.setOnClickListener {
            sortDialog.dismiss()
        }
        btnSortByDate.setOnClickListener {
            try {
                if (isDescending) {
                    questionBankModels.sortByDescending { it.createdDate }
                    bankAdapter.notifyDataSetChanged()
                    isDescending = false
                } else {
                    questionBankModels.sortBy { it.createdDate }
                    bankAdapter.notifyDataSetChanged()
                    isDescending = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sortDialog.dismiss()
        }
    }

    private fun addBank() {
        val addBankDialog = Dialog(this)
        addBankDialog.setContentView(R.layout.dialog_add_bank)
        addBankDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addBankDialog.window?.setGravity(Gravity.CENTER)
        addBankDialog.show()

        val btnBankSubmit = addBankDialog.findViewById<TextView>(R.id.btn_bank_submit)

        val bankMembers: ArrayList<String> = arrayListOf(Constants.userId)
        val bankTitle = addBankDialog.findViewById<EditText>(R.id.bank_title).text.toString()

        btnBankSubmit.setOnClickListener {
            val data = QuestionBankModel(
                Constants.userId, bankTitle,
                "single", LocalDate.now().toString(),
                bankMembers, Constants.userId, Constants.userId,
            )

            if (bankTitle.isEmpty() || bankTitle == null) {
                showErrorSnackBar("名稱不可為空")
            } else {
                showProgressDialog("新增中")
                ConstantsQuestionBankFunction.postQuestionBank(data, this,
                    onSuccess = {
                        Toast.makeText(this, "add bank success", Toast.LENGTH_SHORT).show()
                        Log.d("addBankDialog", "add bank success")
                        addBankDialog.dismiss()
                        bankAdapter.addItem(data)
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
        btnSort = findViewById(R.id.sort_button)
        searchView = findViewById(R.id.search_bar)

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
        settingBankDialog.setCancelable(false)
        settingBankDialog.findViewById<TextView>(R.id.tv_switch_position).visibility = View.GONE
        settingBankDialog.findViewById<View>(R.id.first_dividing_line).visibility = View.GONE
        settingBankDialog.show()

        val btnChangeTitle = settingBankDialog.findViewById<TextView>(R.id.tv_change_title)
        val btnDelete = settingBankDialog.findViewById<TextView>(R.id.tv_delete)
        val btnCancel = settingBankDialog.findViewById<TextView>(R.id.tv_cancel)

        // Show up change title dialog
        btnChangeTitle.setOnClickListener {
            settingBankDialog.dismiss()

            val changeTitleDialog = Dialog(this)
            changeTitleDialog.setContentView(R.layout.dialog_change_title)
            changeTitleDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            changeTitleDialog.window?.setGravity(Gravity.CENTER)
            changeTitleDialog.show()

            val btnSubmit = changeTitleDialog.findViewById<TextView>(R.id.btn_submit)
            val editingHint = changeTitleDialog.findViewById<TextView>(R.id.editing)
            val etTitle = changeTitleDialog.findViewById<EditText>(R.id.et_title)
            etTitle.setText(questionBankModels[position].title)

            var count = 1
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (count % 4 == 0) {
                        editingHint.text = "編輯中"
                    } else {
                        editingHint.append(".")
                    }
                    count++
                    handler.postDelayed(this, 500) // set time here to refresh textView
                }
            })

            val originDescription: String = questionBankModels[position].title
            etTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString() == originDescription) {
                        btnSubmit.visibility = View.GONE
                        isModified = false
                    } else {
                        btnSubmit.visibility = View.VISIBLE
                        isModified = true
                    }
                }
            })

            btnSubmit.setOnClickListener {
                if (isModified) {
                    val data = QuestionBankModel(
                        questionBankModels[position]._id, newBankTitle,
                        questionBankModels[position].questionBankType,
                        questionBankModels[position].createdDate,
                        questionBankModels[position].members,
                        questionBankModels[position].originateFrom,
                        questionBankModels[position].creator
                    )
                    Log.e("BankActivity", "new data = $data")
                    bankAdapter.setItem(position, data)
                    changeTitleDialog.dismiss()
                }
            }
            changeTitleDialog.setOnDismissListener { isModified = false }
        }

        btnDelete.setOnClickListener {
            val deleteWarningDialog = Dialog(this@BankActivity)
            deleteWarningDialog.setContentView(R.layout.dialog_delete_warning)
            deleteWarningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            deleteWarningDialog.window?.setGravity(Gravity.CENTER)
            deleteWarningDialog.show()

            val btnConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_confirm)
            val btnCancelConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_cancel)

            btnConfirm.setOnClickListener {
                bankAdapter.deleteItem(position)
                showSuccessSnackBar("刪除成功")
                deleteWarningDialog.dismiss()
                settingBankDialog.dismiss()
            }
            btnCancelConfirm.setOnClickListener {
                deleteWarningDialog.dismiss()
            }
        }

        btnCancel.setOnClickListener { settingBankDialog.dismiss() }
    }

    override fun updateOption(position: Int, newOption: String) {
//        TODO("Not yet implemented")
    }

}
