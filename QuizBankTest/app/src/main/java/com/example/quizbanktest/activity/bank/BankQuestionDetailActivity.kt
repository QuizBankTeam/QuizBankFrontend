package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.adapters.bank.QuestionOptionsRecyclerViewAdapter
import com.example.quizbanktest.adapters.bank.ViewPagerAdapter
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.qdot.mathrendererlib.MathRenderView


class BankQuestionDetailActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: MathRenderView
    private lateinit var tvAnswerDescription: MathRenderView
    private lateinit var tvImageNumber: TextView
    private lateinit var tvAnswerImageNumber: TextView
    private lateinit var btnShowAnswer: TextView
    private lateinit var btnShowDetail: TextView
    private lateinit var btnAddQuestionImage: ImageButton
    private lateinit var btnAddAnswerImage: ImageButton
    private lateinit var btnAddViewPager: ImageButton
    private lateinit var btnAddAnswerViewPager: ImageButton
    private lateinit var btnRemoveViewPager: ImageButton
    private lateinit var btnRemoveAnswerViewPager: ImageButton
    private lateinit var optionRecyclerView: RecyclerView
    private lateinit var optionAdapter: QuestionOptionsRecyclerViewAdapter
    private lateinit var questionImageViewPager: ViewPager
    private lateinit var questionImageViewPagerAdapter: ViewPagerAdapter
    private lateinit var answerImageViewPager: ViewPager
    private lateinit var answerImageViewPagerAdapter: ViewPagerAdapter

    // Question variable
    private lateinit var questionId: String
    private lateinit var questionTitle: String
    private lateinit var questionNumber: String
    private lateinit var questionDescription: String
    private lateinit var questionOptions: ArrayList<String>
    private lateinit var questionType: String
    private lateinit var bankType: String
    private lateinit var bankId: String
    private lateinit var answerOptions: ArrayList<String>
    private lateinit var answerDescription: String
    private lateinit var questionSource: String
    private lateinit var createdDate: String
    private lateinit var questionImage: ArrayList<String>
    private lateinit var answerImage: ArrayList<String>
    private lateinit var questionTag: ArrayList<String>
    // variable
    private lateinit var newDescription: String
    private lateinit var newAnswerDescription: String
    private var isModified: Boolean = false
    private var isShowingAnswer: Boolean = true
    private var isShowingFillingAnswer: Boolean = false
    private var currentPageIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        init()
        setupOptions()

        btnShowAnswer.setOnClickListener { showAnswer() }

        btnShowDetail.setOnClickListener { showDetail() }

        btnAddQuestionImage.setOnClickListener { addImage(btnAddQuestionImage) }

        btnAddViewPager.setOnClickListener { editViewPager(btnAddViewPager) }

        btnRemoveViewPager.setOnClickListener{ editViewPager(btnRemoveViewPager) }

        tvDescription.setOnClickListener { editDescription() }

        tvAnswerDescription.setOnClickListener { editAnswerDescription() }

        pullExit()
    }

    private fun setupImage() {
        questionImageViewPagerAdapter = ViewPagerAdapter(this@BankQuestionDetailActivity, questionImage)
        questionImageViewPager.adapter = questionImageViewPagerAdapter
        tvImageNumber.text = " "

        //select any page you want as your starting page
        currentPageIndex = 0
        questionImageViewPager.currentItem = currentPageIndex

        // registering for page change callback
        questionImageViewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    //update the image number textview
                    currentPageIndex = position
                    tvImageNumber.text = "${position + 1} / ${questionImage.size}"
                }
            }
        )
    }

    private fun setupOptions() {
        val tmpQuestionOptionsArrayList = ArrayList<String>()
        for (item in questionOptions) {
            tmpQuestionOptionsArrayList.add(item)
        }
        Log.e("BankQuestionDetailActivity", tmpQuestionOptionsArrayList.toString())

        val tmpAnswerOptionsArrayList = ArrayList<String>()
        for (item in answerOptions) {
            tmpAnswerOptionsArrayList.add(item)
        }
        Log.e("BankQuestionDetailActivity", tmpAnswerOptionsArrayList.toString())
        optionRecyclerView = findViewById(R.id.optionRecyclerView)
        optionAdapter = QuestionOptionsRecyclerViewAdapter(this, questionType, tmpQuestionOptionsArrayList, tmpAnswerOptionsArrayList, this)
        optionRecyclerView.setHasFixedSize(true)

        when (questionType) {
            "TrueOrFalse" -> {
                val myLinearLayoutManager = object : LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
                    override fun canScrollHorizontally(): Boolean {
                        return false
                    }
                }
                optionRecyclerView.layoutManager = myLinearLayoutManager
            }
            "Filling" -> {

            }
            else -> {
                optionRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }
        optionRecyclerView.adapter = optionAdapter
    }

    private fun editDescription() {
        val descriptionDialog = Dialog(this)
        descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
        descriptionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        descriptionDialog.window?.setGravity(Gravity.CENTER)
        descriptionDialog.show()

        val etDescription = descriptionDialog.findViewById<EditText>(R.id.et_question_description)
        val btnSubmit = descriptionDialog.findViewById<TextView>(R.id.btn_submit)
        val editingHint = descriptionDialog.findViewById<TextView>(R.id.editing)

        etDescription.setText(newDescription)
        // hint change loop
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

        val originDescription: String = newDescription
        etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == originDescription) {
                    btnSubmit.visibility = View.GONE
                    isModified = false
                } else {
                    btnSubmit.visibility = View.VISIBLE
                    isModified = true
                    newDescription = s.toString()
                }
            }
        })

        btnSubmit.setOnClickListener {
            if (isModified) {
                Log.e("BankQuestionDescriptionDialog", "set new description")
                etDescription.setText(newDescription)
                tvDescription.text = newDescription
                descriptionDialog.dismiss()
            }
        }
    }

    private fun editAnswerDescription() {
        val descriptionDialog = Dialog(this)
        descriptionDialog.setContentView(R.layout.dialog_bank_question_description)
        descriptionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        descriptionDialog.window?.setGravity(Gravity.CENTER)
        descriptionDialog.show()

        val tvTitle = descriptionDialog.findViewById<TextView>(R.id.tv_description_title)
        val etDescription = descriptionDialog.findViewById<EditText>(R.id.et_question_description)
        val btnSubmit = descriptionDialog.findViewById<TextView>(R.id.btn_submit)
        val editingHint = descriptionDialog.findViewById<TextView>(R.id.editing)

        tvTitle.text = "解答描述"
        etDescription.setText(newAnswerDescription)

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

        val originDescription: String = newDescription
        etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == originDescription) {
                    btnSubmit.visibility = View.INVISIBLE
                    isModified = false
                } else {
                    btnSubmit.visibility = View.VISIBLE
                    isModified = true
                    newAnswerDescription = s.toString()
                }
            }
        })

        btnSubmit.setOnClickListener {
            if (isModified) {
                Log.e("BankQuestionDescriptionDialog", "set new description")
                etDescription.setText(newAnswerDescription)
                tvAnswerDescription.text = newAnswerDescription
                descriptionDialog.dismiss()
            }
        }
        descriptionDialog.setOnDismissListener { isModified = false }
    }

    @SuppressLint("SetTextI18n")
    private fun editViewPager(view: View) {
        Log.e("BankQuestionDetailActivity", view.id.toString())
        when (view.id) {
            /** Add image through green add button */
            R.id.btn_add_viewPager, R.id.btn_add_answerViewPager -> {
                if (view.id == R.id.btn_add_viewPager) { addImage(btnAddViewPager) }
                else { addImage(btnAddAnswerViewPager) }
            }
            /** Remove image through red trashcan button */
            R.id.btn_remove_viewPager, R.id.btn_remove_answerViewPager -> {
                val deleteWarningDialog = Dialog(this)
                deleteWarningDialog.setContentView(R.layout.dialog_delete_warning)
                deleteWarningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                deleteWarningDialog.window?.setGravity(Gravity.CENTER)
                deleteWarningDialog.show()

                val btnConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_confirm)
                val btnCancelConfirm = deleteWarningDialog.findViewById<TextView>(R.id.btn_cancel)

                btnConfirm.setOnClickListener {
                    if (view.id == R.id.btn_remove_viewPager) {
                        val position: Int = questionImageViewPager.currentItem
                        val `object`: Any = questionImageViewPager.findViewWithTag("View$position")
                        questionImage.removeAt(position)
//                        questionImageViewPagerAdapter.destroyItem(questionImageViewPager, position, `object`)
                        questionImageViewPagerAdapter.refreshItem()
                        /** update the page number which is at the top-left corner */
                        if (questionImage.size == 0) {
                            tvImageNumber.visibility = View.INVISIBLE
                        } else {
                            tvImageNumber.text = "${position + 1} / ${questionImage.size}"
                        }
                    } else {
                        val position: Int = answerImageViewPager.currentItem
                        val `object`: Any = answerImageViewPager.findViewWithTag("View$position")
                        answerImage.removeAt(position)
//                        answerImageViewPagerAdapter.destroyItem(answerImageViewPager, position, `object`)
                        answerImageViewPagerAdapter.refreshItem()
                        /** update the page number which is at the top-left corner */
                        if (answerImage.size == 0) {
                            tvAnswerImageNumber.visibility = View.INVISIBLE
                        } else {
                            tvAnswerImageNumber.text = "${position + 1} / ${answerImage.size}"
                        }
                    }
                    showSuccessSnackBar("刪除成功")
                    deleteWarningDialog.dismiss()
                    isModified = true
                }
                btnCancelConfirm.setOnClickListener {
                    deleteWarningDialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAnswer() {
        // switch view when clicking show answer button
        if (isShowingAnswer) {
            btnShowAnswer.setBackgroundColor(Color.parseColor("#ff7575"))
            btnShowAnswer.text = "隱藏答案"
            btnShowDetail.visibility = View.VISIBLE
        }
        else {
            btnShowAnswer.setBackgroundColor(Color.parseColor("#c6fa73"))
            btnShowAnswer.text = "顯示答案"
            btnShowDetail.visibility = View.INVISIBLE
        }
        isShowingAnswer = !isShowingAnswer

        // decide the style of answer presentation in different type of question
        if (questionType == "Filling") {
            if (!isShowingFillingAnswer) {
                tvAnswerDescription.visibility = View.VISIBLE
            }
            else {
                tvAnswerDescription.visibility = View.INVISIBLE
            }
            isShowingFillingAnswer  = !isShowingFillingAnswer

        } else {
            optionAdapter.showAnswer()
            optionAdapter.notifyDataSetChanged()
        }
    }

    private fun addImage(view: View) {
        choosePhotoFromGallery { bitmap ->
            if (bitmap != null) {
                val base64String = ConstantsFunction.encodeImage(bitmap)
                when (view.id) {
                    /** Add image through empty_image.xml imageButton */
                    R.id.btn_add_questionImage, R.id.btn_add_answerImage -> {
                        if (view.id == R.id.btn_add_questionImage) {
                            btnAddViewPager.visibility = View.VISIBLE
                            btnRemoveViewPager.visibility = View.VISIBLE
                            tvImageNumber.visibility = View.VISIBLE
                            questionImage.add(base64String!!)
                            questionImageViewPagerAdapter.refreshItem()
                            btnAddQuestionImage.visibility = View.INVISIBLE
                            tvImageNumber.text = "1 / 1"
                        } else {
                            btnAddAnswerViewPager.visibility = View.VISIBLE
                            btnRemoveAnswerViewPager.visibility = View.VISIBLE
                            tvAnswerImageNumber.visibility = View.VISIBLE
                            answerImage.add(base64String!!)
                            answerImageViewPagerAdapter.refreshItem()
                            btnAddAnswerImage.visibility = View.INVISIBLE
                            tvAnswerImageNumber.text = "1 / 1"
                        }
                    }
                    /** Add image through green add imageButton */
                    R.id.btn_add_viewPager, R.id.btn_add_answerViewPager -> {
                        if (view.id == R.id.btn_add_viewPager) {
                            questionImage.add(base64String!!)
                            questionImageViewPagerAdapter.refreshItem()
                        } else {
                            answerImage.add(base64String!!)
                            answerImageViewPagerAdapter.refreshItem()
                        }
                    }
                }
            }
        }
        isModified = true
    }

    private fun showDetail() {
        val detailDialog = Dialog(this)
        detailDialog.setContentView(R.layout.dialog_bank_question_detail)
        detailDialog.window?.setGravity(Gravity.CENTER)
        detailDialog.show()

        val tvDescription = detailDialog.findViewById<TextView>(R.id.answer_description)
        tvAnswerImageNumber = detailDialog.findViewById(R.id.imageNumberTV)
        btnAddAnswerViewPager = detailDialog.findViewById(R.id.btn_add_answerViewPager)
        btnRemoveAnswerViewPager = detailDialog.findViewById(R.id.btn_remove_answerViewPager)
        btnAddAnswerImage = detailDialog.findViewById(R.id.btn_add_answerImage)
        answerImageViewPager = detailDialog.findViewById(R.id.viewPager)
        answerImageViewPagerAdapter = ViewPagerAdapter(this, answerImage)

        tvAnswerImageNumber.text = " "
        tvDescription.movementMethod = ScrollingMovementMethod()
        tvDescription.text = answerDescription
        tvDescription.setOnClickListener{ editAnswerDescription() }

        if (answerImage.isEmpty()) {
            tvAnswerImageNumber.visibility = View.INVISIBLE
            btnAddAnswerImage.visibility = View.VISIBLE
            btnAddAnswerViewPager.visibility = View.INVISIBLE
            btnRemoveAnswerViewPager.visibility = View.INVISIBLE
            showEmptySnackBar("目前照片為空喔")
        } else {
            btnAddAnswerImage.visibility = View.INVISIBLE
            answerImageViewPager.adapter = answerImageViewPagerAdapter

            //select any page you want as your starting page
            val currentPageIndex = 0
            answerImageViewPager.currentItem = currentPageIndex

            answerImageViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    @SuppressLint("SetTextI18n")
                    override fun onPageSelected(position: Int) {
                        // update the image number textview when switching pages
                        tvImageNumber.text = "${position + 1} / ${answerImage.size}"
                    }
                }
            )
        }

        btnAddAnswerViewPager.setOnClickListener { editViewPager(btnAddAnswerViewPager) }
        btnRemoveAnswerViewPager.setOnClickListener { editViewPager(btnRemoveAnswerViewPager) }
        btnAddAnswerImage.setOnClickListener { addImage(btnAddAnswerImage) }
    }

    private fun putQuestion() {
        if (isModified) {
            AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle("注意")
                .setMessage("是否儲存編輯?")
                .setPositiveButton("好") { _, _ ->
                    Toast.makeText(applicationContext, "好", Toast.LENGTH_SHORT).show()
                    val putQuestionBody = QuestionModel(
                        questionId, questionTitle, questionNumber, questionDescription,
                        questionOptions, questionType, bankType, bankId, answerOptions,
                        answerDescription, questionSource, createdDate, questionImage,
                        answerImage, questionTag
                    )
                    ConstantsQuestionFunction.putQuestion(this, putQuestionBody,
                        onSuccess = {
                            Log.e("BankQuestionDetailActivity", "upload success")
                        },
                        onFailure = { errorMessage ->
                            showErrorSnackBar("網路連線狀況不好")
                            hideProgressDialog()
                            Log.e("BankQuestionDetailActivity", "Error Message: $errorMessage")
                        }
                    )
                    this.finish()
                }
                .setNeutralButton("繼續編輯") { _, _ ->
                    Toast.makeText(applicationContext, "繼續編輯", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消") { _, _ ->
                    Toast.makeText(applicationContext, "取消", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                .setCancelable(false)
                .show()
                .window?.setLayout(1000, 600)
        } else {
            Log.e("BankQuestionDetailActivity", "didn't modify")
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

    fun backToPreviousPage(view: View?) {
        if (isModified) {
            putQuestion()
        } else {
            finish()
        }
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        if (isModified) {
            putQuestion()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isModified) {
            putQuestion()
        } else {
            finish()
        }
    }

    private fun init() {
        // Question variable initialization
        questionId = intent.getStringExtra("id").toString()
        questionTitle = intent.getStringExtra("title").toString()
        questionNumber = intent.getStringExtra("number").toString()
        questionDescription = intent.getStringExtra("description").toString()

        val questionOptions = intent.getStringArrayListExtra("options")
        if (questionOptions != null) {
            this.questionOptions = questionOptions
        }

        questionType = intent.getStringExtra("type").toString()
        bankType = intent.getStringExtra("bankType").toString()

        bankId = intent.getStringExtra("bankId").toString()
        val answerOptions = intent.getStringArrayListExtra("answerOptions")
        if (answerOptions != null) {
            this.answerOptions = answerOptions
            answerOptions.add("roughly")
        }

        answerDescription = intent.getStringExtra("answerDescription").toString()
        questionSource = intent.getStringExtra("source").toString()
        createdDate = intent.getStringExtra("createdDate").toString()

        val questionImage = intent.getStringArrayListExtra("image")
        if (questionImage != null) {
            this.questionImage = questionImage
        }

        val answerImage = intent.getStringArrayListExtra("answerImage")
        if (answerImage != null) {
            this.answerImage = answerImage
        }

        val questionTag = intent.getStringArrayListExtra("tag")
        if (questionTag != null) {
            this.questionTag = questionTag
        }

        // View initialization
        tvTitle = findViewById(R.id.question_title)
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)
        tvAnswerDescription = findViewById(R.id.answer_description)
        btnAddQuestionImage = findViewById(R.id.btn_add_questionImage)
        btnShowAnswer = findViewById(R.id.btn_show_answer)
        btnShowDetail = findViewById(R.id.btn_show_detail)
        btnAddViewPager = findViewById(R.id.btn_add_viewPager)
        btnRemoveViewPager = findViewById(R.id.btn_remove_viewPager)
        tvImageNumber = findViewById(R.id.imageNumberTV)
        questionImageViewPager = findViewById(R.id.viewPager)

        tvTitle.text = questionTitle
        if (tvTitle.length() < 10) {
            tvTitle.isHorizontalScrollBarEnabled = false
            tvTitle.gravity = Gravity.CENTER
        } else {
            tvTitle.movementMethod = ScrollingMovementMethod()
            tvTitle.isSelected = true
        }

        tvType.text = questionType
        tvType.setBackgroundColor(Color.parseColor("#ffeb3b"))
        tvDescription.text = questionDescription
        // code below is commented due to the mathRenderView
//        tvDescription.movementMethod = ScrollingMovementMethod()
        tvAnswerDescription.text = answerDescription
        // code below is commented due to the mathRenderView
//        tvAnswerDescription.movementMethod = ScrollingMovementMethod()

        // Variable initialization
        newDescription = questionDescription
        newAnswerDescription = answerDescription

        if (questionImage!!.isEmpty()) {
            tvImageNumber.visibility = View.INVISIBLE
            btnAddQuestionImage.visibility = View.VISIBLE
            btnAddViewPager.visibility = View.INVISIBLE
            btnRemoveViewPager.visibility = View.INVISIBLE
            showEmptySnackBar("目前照片為空喔")
        } else {
            tvImageNumber.visibility = View.VISIBLE
            btnAddQuestionImage.visibility = View.INVISIBLE
            btnAddViewPager.visibility = View.VISIBLE
            btnRemoveViewPager.visibility = View.VISIBLE
        }
        setupImage()
    }

    override fun onItemClick(position: Int) {}
    override fun switchBank(newBankPosition: Int) {}
    override fun settingCard(position: Int) {}

    override fun updateOption(position: Int, newOption: String) {
        // update question option's description data
        questionOptions[position] = newOption
        isModified = true
    }
}