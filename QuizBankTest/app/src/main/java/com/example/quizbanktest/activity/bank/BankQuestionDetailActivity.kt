package com.example.quizbanktest.activity.bank

import android.annotation.SuppressLint
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
import com.example.quizbanktest.utils.ConstantsQuestionFunction


class BankQuestionDetailActivity : BaseActivity(), RecyclerViewInterface {
    // View variable
    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvAnswerDescription: TextView
    private lateinit var tvImageNumber: TextView
    private lateinit var btnShowAnswer: TextView
    private lateinit var btnShowDetail: TextView
    private lateinit var optionRecyclerView: RecyclerView
    private lateinit var optionAdapter: QuestionOptionsRecyclerViewAdapter
    private lateinit var imageViewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_question_detail)

        init()
        setupOptions()

        btnShowAnswer.setOnClickListener { showAnswer() }

        btnShowDetail.setOnClickListener { showDetail() }

        tvDescription.setOnClickListener { editDescription() }

        tvAnswerDescription.setOnClickListener { editAnswerDescription() }

        pullExit()
    }

    private fun setupImage() {
        viewPagerAdapter = ViewPagerAdapter(this@BankQuestionDetailActivity, questionImage)
        imageViewPager.adapter = viewPagerAdapter
        tvImageNumber.text = " "

        //select any page you want as your starting page
        val currentPageIndex = 0
        imageViewPager.currentItem = currentPageIndex

        // registering for page change callback
        imageViewPager.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    //update the image number textview
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
                optionRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
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

        val etDescription = descriptionDialog.findViewById<EditText>(R.id.et_question_description)
        val btnSubmit = descriptionDialog.findViewById<TextView>(R.id.btn_submit)
        val editingHint = descriptionDialog.findViewById<TextView>(R.id.editing)

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
                    btnSubmit.visibility = View.GONE
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAnswer() {
        // switch view while we are clicking button
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

    private fun showDetail() {
        val detailDialog = Dialog(this)
        detailDialog.setContentView(R.layout.dialog_bank_question_detail)
        detailDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        detailDialog.window?.setGravity(Gravity.CENTER)
        detailDialog.show()

        val tvDescription = detailDialog.findViewById<TextView>(R.id.answer_description)
        val answerImageViewPager = detailDialog.findViewById<ViewPager>(R.id.viewPager)
        val answerViewPagerAdapter = ViewPagerAdapter(this, answerImage)
        val tvImageNumber = detailDialog.findViewById<TextView>(R.id.imageNumberTV)
        tvImageNumber.text = " "

        tvDescription.movementMethod = ScrollingMovementMethod()
        tvDescription.text = answerDescription
        tvDescription.setOnClickListener{ editAnswerDescription() }

        if (answerImage.isEmpty()) {
            answerImageViewPager.visibility = View.INVISIBLE
            tvImageNumber.visibility = View.GONE
            detailDialog.findViewById<ImageView>(R.id.empty_box).visibility = View.VISIBLE
            showEmptySnackBar("目前照片為空喔")
        } else {
            detailDialog.findViewById<ImageView>(R.id.empty_box).visibility = View.INVISIBLE

            answerImageViewPager.adapter = answerViewPagerAdapter
            //select any page you want as your starting page
            val currentPageIndex = 0
            answerImageViewPager.currentItem = currentPageIndex

            answerImageViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onPageSelected(position: Int) {
                        //update the image number textview
                        tvImageNumber.text = "${position + 1} / ${questionImage.size}"
                    }
                }
            )
        }
    }

    private fun putQuestion() {
        Log.e("BankQuestionDetailActivity", "put question")
        if (isModified) {
            val putQuestionBody = QuestionModel(questionId, questionTitle, questionNumber, questionDescription, questionOptions, questionType, bankType, bankId, answerOptions, answerDescription, questionSource, createdDate, questionImage, answerImage, questionTag)
            ConstantsQuestionFunction.putQuestion(this, putQuestionBody,
                onSuccess = {
                    Log.e("BankQuestionDetailActivity", "upload success")
                },
                onFailure = { errorMessage ->
                    showErrorSnackBar("網路連線狀況不好")
                    hideProgressDialog()
                    Log.e("BankQuestionDetailActivity", "Error Message: $errorMessage" )
                }
            )
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
        putQuestion()
        this.finish()
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        putQuestion()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        putQuestion()
        finish()
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

        Log.e("BankQuestionDetailActivity", "image: $questionImage")
        Log.e("BankQuestionDetailActivity", "answerImage: $answerImage")

        // View initialization
        tvTitle = findViewById(R.id.question_title)
        tvType = findViewById(R.id.question_type)
        tvDescription = findViewById(R.id.question_description)
        tvAnswerDescription = findViewById(R.id.answer_description)
        btnShowAnswer = findViewById(R.id.btn_show_answer)
        btnShowDetail = findViewById(R.id.btn_show_detail)
        tvImageNumber = findViewById(R.id.imageNumberTV)
        imageViewPager = findViewById(R.id.viewPager)

        tvTitle.text = questionTitle
        tvTitle.movementMethod = ScrollingMovementMethod()
        tvTitle.isSelected = true
        tvType.text = questionType
        tvDescription.movementMethod = ScrollingMovementMethod()
        tvDescription.text = questionDescription
        tvAnswerDescription.movementMethod = ScrollingMovementMethod()
        tvAnswerDescription.text = answerDescription

        // Variable initialization
        newDescription = questionDescription
        newAnswerDescription = answerDescription

        if (questionImage!!.isEmpty()) {
            imageViewPager.visibility = View.INVISIBLE
            tvImageNumber.visibility = View.INVISIBLE
            findViewById<RelativeLayout>(R.id.image_layout).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.img_empty).visibility = View.VISIBLE
            showEmptySnackBar("目前照片為空喔")
        } else {
            setupImage()
        }
    }

    override fun onItemClick(position: Int) {}
    override fun switchBank(position: Int) {}
    override fun settingCard() {}

    override fun updateOption(position: Int, newOption: String) {
        // update question option's description data
        questionOptions[position] = newOption
    }
}