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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.*
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.scan.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.adapters.bank.QuestionRecyclerViewAdapter
import com.example.quizbanktest.adapters.bank.SwitchBankViewAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.example.quizbanktest.view.WrapLayout
import com.google.android.material.card.MaterialCardView
import org.w3c.dom.Text
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class BankQuestionActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var searchView: SearchView
    private lateinit var tvTitle: TextView
    private lateinit var btnBackArrow: ImageButton
    private lateinit var btnAddQuestion: ImageButton
    private lateinit var btnSort: ImageButton
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var questionAdapter: QuestionRecyclerViewAdapter
    private lateinit var switchBankRecyclerView: RecyclerView
    private lateinit var switchBankAdapter: SwitchBankViewAdapter
    private lateinit var switchPositionDialog : Dialog

    // Question variable
    private var questionModels = ArrayList<QuestionModel>()
    private var allQuestionBanks = ArrayList<QuestionBankModel>()
    private var tagQuestionModels = ArrayList<QuestionModel>()

    // Variable
    private lateinit var bankTitle: String
    private lateinit var bankId: String
    private val allTagList: ArrayList<String> = ArrayList()
    private val showingTagList: ArrayList<String> = ArrayList()
    private var wrapLayout: WrapLayout? = null
    private lateinit var newQuestionTitle: String
    private lateinit var newQuestionType: String
    private lateinit var newQuestionDate: String
    private var isModified: Boolean = false
    private var isDescending: Boolean = true
    private var switchTargetPosition = -1
    private val backGroundArray: ArrayList<String> = arrayListOf(
        "#E6CAFF", "#FFB5B5", "#CECEFF", "#ACD6FF", "#FFF0AC", "#FFCBB3"
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question)
//        setupNavigationView()
        doubleCheckExit()

        init()
        btnAddQuestion = findViewById(R.id.question_add)
        btnAddQuestion.setOnClickListener {
            val addQuestionActivity = Intent(this, ScannerTextWorkSpaceActivity::class.java)
            startActivity(addQuestionActivity)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                /**調用RecyclerView內的Filter方法 */
                questionAdapter.getFilter().filter(newText)
                return false
            }
        })

        btnSort.setOnClickListener { setSortDialog() }

        pullExit()
    }
    private fun setupQuestionModel() {
        Log.e("BankQuestionActivity", "set up question model")
        questionRecyclerView = findViewById(R.id.questionRecyclerView)
        questionAdapter = QuestionRecyclerViewAdapter(this, this, questionModels, this)

        questionRecyclerView.adapter = questionAdapter
        questionRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun setSortDialog() {
        val sortDialog = Dialog(this)
        sortDialog.setContentView(R.layout.dialog_sort_tags)
        sortDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sortDialog.window?.setGravity(Gravity.CENTER)
        sortDialog.show()

        /** views init */
        val btnSortByName = sortDialog.findViewById<TextView>(R.id.tv_sortByName)
        val btnSortByDate = sortDialog.findViewById<TextView>(R.id.tv_sortByDate)

        /** tags init */
        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wrapLayout = sortDialog.findViewById(R.id.clip_layout)
        if (allTagList.isEmpty()) {
            Log.e("BankQuestionActivity", "No tags inside, using the example")
            val example = arrayOf(
                "作業系統", "離散數學", "線性代數", "資料結構",
                "演算法", "計算機組織", "python", "java"
            )
            for (item in example) {
                val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
                val tagName = itemLayout.findViewById<View>(R.id.name) as TextView
                val isClickedBefore = itemLayout.findViewById<View>(R.id.check) as TextView
                val cardView = itemLayout.findViewById<MaterialCardView>(R.id.cardView)
                isClickedBefore.text = "0"
                tagName.text = item
                tagName.setBackgroundColor(Color.parseColor(backGroundArray[(0..5).random()]))
                tagName.setTextColor(Color.parseColor("#FCFCFC"))
                tagName.setOnClickListener {
                    if (isClickedBefore.text == "1") {
                        cardView.strokeColor = Color.TRANSPARENT
                        isClickedBefore.text = "0"
                        showingTagList.remove(tagName.text.toString())
                    } else {
                        cardView.strokeColor = Color.parseColor("#8ecae6")
                        isClickedBefore.text = "1"
                        showingTagList.add(tagName.text.toString())
                    }
                    Log.e("BankQuestionDetailActivity", "All tags you pick now: $showingTagList")
                }
                wrapLayout!!.addView(itemLayout)
            }
        } else {
            Log.e("BankQuestionActivity", "Here are all the tags below:\n$allTagList")
            var isRepeated = false
            for (item in allTagList) {
                isRepeated = false
                /** Check if there are the same tags inside */
                for (j in allTagList.indices) {
                    if (item == allTagList[j]) {
                        isRepeated = true
                        break
                    }
                }
                if (isRepeated) { continue }
                else {
                    val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
                    val tagName = itemLayout.findViewById<View>(R.id.name) as TextView
                    val isClickedBefore = itemLayout.findViewById<TextView>(R.id.check)
                    val cardView = itemLayout.findViewById<MaterialCardView>(R.id.cardView)
                    isClickedBefore.text = "0"
                    tagName.text = item
                    tagName.setBackgroundColor(Color.parseColor(backGroundArray[(0..5).random()]))
                    tagName.setTextColor(Color.parseColor("#FCFCFC"))
                    tagName.setOnClickListener {
                        if (isClickedBefore.text == "1") {
                            /** The tag was clicked before, convert the border into transparent */
                            cardView.strokeColor = Color.TRANSPARENT
                            isClickedBefore.text = "0"
                            showingTagList.remove(tagName.text.toString())
                        } else {
                            /** The tag wasn't clicked before, the border still transparent */
                            cardView.strokeColor = Color.parseColor("#8ECAE6")
                            isClickedBefore.text = "1"
                            showingTagList.add(tagName.text.toString())
                        }
                        Log.e("BankQuestionDetailActivity", "All tags you pick now: $showingTagList")
                        tagQuestionModels.addAll(questionModels)
                        /** select those questions that contain the tags which were chosen above */
                        for (showingTag in showingTagList) {
                            for (i in tagQuestionModels.indices) {
                                for (tag in tagQuestionModels[i].tag) {
                                    if (tag != showingTag) { tagQuestionModels.removeAt(i) }
                                }
                            }
                        }
                        /** refresh the view */
                        questionAdapter = QuestionRecyclerViewAdapter(
                            this, this, tagQuestionModels, this)
                    }
                    wrapLayout!!.addView(itemLayout)
                }
            }
        }

        /** Listener area */
        btnSortByName.setOnClickListener {
            sortDialog.dismiss()
        }
        btnSortByDate.setOnClickListener {
            try {
                if (isDescending) {
                    questionModels.sortByDescending { it.createdDate }
                    questionAdapter.notifyDataSetChanged()
                    isDescending = false
                } else {
                    questionModels.sortBy { it.createdDate }
                    questionAdapter.notifyDataSetChanged()
                    isDescending = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BankQuestionActivity", "Convert date fail")
            }
            sortDialog.dismiss()
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

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() { finish() }

    fun backToPreviousPage(view: View?) { finish() }

    override fun onBackPressed() { finish() }

    fun init() {
        bankTitle = intent.getStringExtra("BankTitle").toString()
        bankId = intent.getStringExtra("BankId").toString()

        searchView = findViewById(R.id.search_bar)
        tvTitle = findViewById(R.id.title)
        btnBackArrow = findViewById(R.id.btn_back_arrow)
        btnAddQuestion = findViewById(R.id.question_add)
        btnSort = findViewById(R.id.sort_button)

        tvTitle.text = bankTitle

        showProgressDialog("取得資料中")
        ConstantsQuestionFunction.getQuestion(this, bankId,
            onSuccess = { questionList ->
                if (questionList.isEmpty()) {
                    findViewById<ImageView>(R.id.img_empty).visibility = View.VISIBLE
                    showEmptySnackBar("裡面沒有題目喔~")
                    hideProgressDialog()
                } else {
                    try {
                        for (item in questionList) {
                            if (item.questionImage == null) {
                                val image : ArrayList<String> = ArrayList()
                                item.questionImage = image
                            }

                            if (item.answerImage == null) {
                                val answerImage : ArrayList<String> = ArrayList()
                                item.answerImage = answerImage
                            }
                            val questionModel = QuestionModel(
                                item._id, item.title, item.number, item.description,
                                item.options, item.questionType, item.bankType, item.questionBank,
                                item.answerOptions, item.answerDescription, item.originateFrom,
                                item.createdDate, item.questionImage, item.answerImage, item.tag
                            )

                            for (i in item.tag) {
                                for (j in allTagList.indices)
                                    if (i != allTagList[j]) { allTagList.add(i) }
                            }

                            questionModels.add(questionModel)
                        }

                        setupQuestionModel()
                        hideProgressDialog()
                    } catch (e: Exception) {
                        Log.e("BankQuestionActivity", "error message: $e")
                    }
                }
            },
            onFailure = {
                findViewById<ImageView>(R.id.img_empty).visibility = View.VISIBLE
                showEmptySnackBar("裡面沒有資料喔")
                hideProgressDialog()
            }
        )
    }

    override fun onItemClick(position: Int) {
        val questionDetailActivity = Intent(this, BankQuestionDetailActivity::class.java)

        questionDetailActivity.putExtra("id", questionModels[position]._id)
        questionDetailActivity.putExtra("title", questionModels[position].title)
        questionDetailActivity.putExtra("number", questionModels[position].number)
        questionDetailActivity.putExtra("description", questionModels[position].description)
        questionDetailActivity.putExtra("options", questionModels[position].options)
        questionDetailActivity.putExtra("type", questionModels[position].questionType)
        questionDetailActivity.putExtra("answerOptions", questionModels[position].answerOptions)
        questionDetailActivity.putExtra("answerDescription", questionModels[position].answerDescription)
        questionDetailActivity.putExtra("source", questionModels[position].originateFrom)
        questionDetailActivity.putExtra("createdDate", questionModels[position].createdDate)
        questionDetailActivity.putExtra("image", questionModels[position].questionImage)
//        QuestionDetailActivity.putExtra("answerImage", questionModels[position].answerImage)
        questionDetailActivity.putStringArrayListExtra("answerImage", questionModels[position].answerImage)
        questionDetailActivity.putExtra("tag", questionModels[position].tag)

        startActivity(questionDetailActivity)
    }

    override fun settingCard(position: Int) {
        switchTargetPosition = position

        // set up dialog
        val settingQuestionDialog = Dialog(this)
        settingQuestionDialog.setContentView(R.layout.dialog_setting_panel)
        settingQuestionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        settingQuestionDialog.window?.setGravity(Gravity.CENTER)
        settingQuestionDialog.setCancelable(false)
        settingQuestionDialog.show()

        // init id
        val btnChangeTitle = settingQuestionDialog.findViewById<TextView>(R.id.tv_change_title)
        val btnSwitchPosition = settingQuestionDialog.findViewById<TextView>(R.id.tv_switch_position)
        val btnDelete = settingQuestionDialog.findViewById<TextView>(R.id.tv_delete)
        val btnCancel = settingQuestionDialog.findViewById<TextView>(R.id.tv_cancel)

        // Show up change title dialog
        btnChangeTitle.setOnClickListener {
            settingQuestionDialog.dismiss()

            val changeTitleDialog = Dialog(this)
            changeTitleDialog.setContentView(R.layout.dialog_change_title)
            changeTitleDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            changeTitleDialog.window?.setGravity(Gravity.CENTER)
            changeTitleDialog.show()

            val btnSubmit = changeTitleDialog.findViewById<TextView>(R.id.btn_submit)
            val editingHint = changeTitleDialog.findViewById<TextView>(R.id.editing)
            val etTitle = changeTitleDialog.findViewById<EditText>(R.id.et_title)
            etTitle.setText(questionModels[position].title)

            var count = 1
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (count % 4 == 0) {
                        editingHint.setText("編輯中")
                    } else {
                        editingHint.append(".")
                    }
                    count++
                    handler.postDelayed(this, 500) // set time here to refresh textView
                }
            })

            // title text listener
            val originDescription: String = questionModels[position].title
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

            // put new data to backend
            btnSubmit.setOnClickListener {
                if (isModified) {
                    val data = QuestionModel(
                        questionModels[position]._id, newQuestionTitle,
                        questionModels[position].number, questionModels[position].description,
                        questionModels[position].options, questionModels[position].questionType,
                        questionModels[position].bankType, questionModels[position].questionBank,
                        questionModels[position].answerOptions, questionModels[position].answerDescription,
                        questionModels[position].originateFrom, questionModels[position].createdDate,
                        questionModels[position].questionImage, questionModels[position].answerImage,
                        questionModels[position].tag)
                    questionAdapter.setItem(position, data)
                    changeTitleDialog.dismiss()
                }
            }
            changeTitleDialog.setOnDismissListener { isModified = false }
        }

        // Show up switch bank dialog
        btnSwitchPosition.setOnClickListener {
            settingQuestionDialog.dismiss()

            switchPositionDialog = Dialog(this)
            switchPositionDialog.setContentView(R.layout.dialog_switch_position)
            switchPositionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            switchPositionDialog.window?.setGravity(Gravity.CENTER)

            // Get all bank to initialize recyclerview with filling bank name
            ConstantsQuestionBankFunction.getAllUserQuestionBanks(this,
                onSuccess = { questionBanks ->
                    Log.e("BankQuestionActivity", "There are ${questionBanks.size} banks available")
                    allQuestionBanks = questionBanks
                    switchBankRecyclerView = switchPositionDialog.findViewById(R.id.switchBankRecyclerView)
                    switchBankAdapter = SwitchBankViewAdapter(this, this, questionBanks, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
                    switchBankRecyclerView.layoutManager = mLayoutManager
                    switchBankRecyclerView.adapter = switchBankAdapter
                    switchBankRecyclerView.addItemDecoration (
                        DividerItemDecoration (
                            this,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
            switchPositionDialog.show()
        }

        // Show up delete bank dialog
        btnDelete.setOnClickListener {
            val deleteWarningDialog = Dialog(this@BankQuestionActivity)
            deleteWarningDialog.setContentView(R.layout.dialog_delete_warning)
            deleteWarningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            deleteWarningDialog.window?.setGravity(Gravity.CENTER)
            deleteWarningDialog.show()

            val btnConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_confirm)
            val btnCancelConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_cancel)

            btnConfirm.setOnClickListener {
                questionAdapter.deleteItem(position)
                showSuccessSnackBar("刪除成功")
                deleteWarningDialog.dismiss()
                settingQuestionDialog.dismiss()
            }
            btnCancelConfirm.setOnClickListener {
                deleteWarningDialog.dismiss()
            }
        }

        btnCancel.setOnClickListener { settingQuestionDialog.dismiss() }
    }

    // Here's switching question to specific bank after clicking confirm button
    override fun switchBank(newBankPosition: Int) {
        val btnSubmit = switchPositionDialog.findViewById<TextView>(R.id.btn_submit)
        btnSubmit.visibility = View.VISIBLE
        btnSubmit.setOnClickListener {
            switchPositionDialog.dismiss()
            ConstantsQuestionFunction.moveQuestion(
                this, questionModels[switchTargetPosition]._id, allQuestionBanks[newBankPosition]._id,
                onSuccess = {
                    questionAdapter.moveItem(switchTargetPosition)
                    showSuccessSnackBar("成功")
                },
                onFailure = {
                    showErrorSnackBar("無效")
                }
            )
        }
    }

    override fun updateOption(position: Int, newOption: String) {
        // No need to define this function, this function is for question detail activity
    }
}