package com.example.quizbanktest.adapters.scan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.introducemyself.utils.ConstantsOcrResults.rescanPosition
import com.example.introducemyself.utils.ConstantsTag
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.scan.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.ScanQuestionModel
import com.example.quizbanktest.utils.*
import com.qdot.mathrendererlib.TextAlign
import java.io.ByteArrayOutputStream


class OcrResultViewAdapter(
    private val activity: BaseActivity,
    private val context: Context,
    private var list: ArrayList<ScanQuestionModel>,
    private var optionList :  Pair<String, List<String>>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_scanner_text,
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var tagQuestionList:ArrayList<String> = ArrayList()//顯示有哪些題目標籤
        if(ConstantsOcrResults.questionList[position].tag?.isNotEmpty() == true){
            tagQuestionList = ConstantsOcrResults.questionList[position].tag!!
        }
        var imageList : ArrayList<String> = ArrayList() //目前選擇了那些圖片(包括 題目描述跟答案之圖片)
        if(ConstantsOcrResults.questionList[position].image?.isNotEmpty() == true){
            imageList = ConstantsOcrResults.questionList[position].image!!
        }
        val out = ByteArrayOutputStream()
        var optionsNum : Int = 1 //預設選項為四個
        val model = list[position] //知道目前是哪個東西被選擇
        //去給他初始化
        if(tagQuestionList.size == 0){
            holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags = ConstantsTag.getEmptyList()
        }else{
            holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags = tagQuestionList
        }


        //題目的標題
        val title : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_title)
        if(ConstantsOcrResults.questionList[position].title?.isNotEmpty() == true){
            title.setText(ConstantsOcrResults.questionList[position].title)
        }
        title.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if(title.text.isNotEmpty()){
                    ConstantsOcrResults.questionList[position].title = title.text.toString()
                }

            }
        }
        var isSingleChoice = false
        var isTrueFalse = false
        //題目的題號
        val questionNum : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_num)
        if(ConstantsOcrResults.questionList[position].number?.isNotEmpty() == true){
            questionNum.setText(ConstantsOcrResults.questionList[position].number)
        }
        questionNum.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if(questionNum.text.isNotEmpty()){
                    ConstantsOcrResults.questionList[position].number = questionNum.text.toString()
                }

            }
        }
        val questionTypeSpinner : Spinner = holder.itemView.findViewById(R.id.spinner_question_type)
        var mAdapter = QuestionTypeAdapter(activity, ConstantsOcrResults.questionTypeSpinner)
        questionTypeSpinner.setAdapter(mAdapter)
        questionTypeSpinner.setSelection(0, false)

        if (holder is MyViewHolder) {
            //一題最多只能有十個選項
            val btnScanPhoto : ImageButton  = holder.itemView.findViewById(R.id.btn_scan_photo)
            if(ConstantsOcrResults.questionList[position].image?.isNotEmpty()==true){
                btnScanPhoto.setImageResource(R.drawable.afteraddimage)
            }
            val option1 : EditText = holder.itemView.findViewById(R.id.question_option1)
            val option2 : EditText = holder.itemView.findViewById(R.id.question_option2)
            val option3 : EditText = holder.itemView.findViewById(R.id.question_option3)
            val option4 : EditText = holder.itemView.findViewById(R.id.question_option4)
            val option5 : EditText = holder.itemView.findViewById(R.id.question_option5)
            val option6 : EditText = holder.itemView.findViewById(R.id.question_option6)
            val option7 : EditText = holder.itemView.findViewById(R.id.question_option7)
            val option8 : EditText = holder.itemView.findViewById(R.id.question_option8)
            val option9 : EditText = holder.itemView.findViewById(R.id.question_option9)
            val option10 : EditText = holder.itemView.findViewById(R.id.question_option10)
            val optionList : LinearLayout = holder.itemView.findViewById(R.id.options_list)
            val trueFalseLayout : LinearLayout = holder.itemView.findViewById(R.id.true_false)
            val checkBoxTint : TextView = holder.itemView.findViewById(R.id.checkbox_tint)
            val checkBox1 = holder.itemView.findViewById<CheckBox>(R.id.answer_option1_check)
            val checkBox2 = holder.itemView.findViewById<CheckBox>(R.id.answer_option2_check)
            val checkBox3 = holder.itemView.findViewById<CheckBox>(R.id.answer_option3_check)
            val checkBox4= holder.itemView.findViewById<CheckBox>(R.id.answer_option4_check)
            val checkBox5 = holder.itemView.findViewById<CheckBox>(R.id.answer_option5_check)
            val checkBox6 = holder.itemView.findViewById<CheckBox>(R.id.answer_option6_check)
            val checkBox7 = holder.itemView.findViewById<CheckBox>(R.id.answer_option7_check)
            val checkBox8 = holder.itemView.findViewById<CheckBox>(R.id.answer_option8_check)
            val checkBox9= holder.itemView.findViewById<CheckBox>(R.id.answer_option9_check)
            val checkBox10 = holder.itemView.findViewById<CheckBox>(R.id.answer_option10_check)
            val checkBoxForTrue = holder.itemView.findViewById<CheckBox>(R.id.true_false_true_check)
            val checkBoxForFalse = holder.itemView.findViewById<CheckBox>(R.id.true_false_false_check)
            val optionNum2 = holder.itemView.findViewById<TextView>(R.id.option_num2)
            val optionNum3 = holder.itemView.findViewById<TextView>(R.id.option_num3)
            val optionNum4 = holder.itemView.findViewById<TextView>(R.id.option_num4)
            val optionNum5= holder.itemView.findViewById<TextView>(R.id.option_num5)
            val optionNum6 = holder.itemView.findViewById<TextView>(R.id.option_num6)
            val optionNum7 = holder.itemView.findViewById<TextView>(R.id.option_num7)
            val optionNum8 = holder.itemView.findViewById<TextView>(R.id.option_num8)
            val optionNum9 = holder.itemView.findViewById<TextView>(R.id.option_num9)
            val optionNum10 = holder.itemView.findViewById<TextView>(R.id.option_num10)
            var optionLayout2 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout2)
            var optionLayout3 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout3)
            var optionLayout4 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout4)
            var optionLayout5 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout5)
            var optionLayout6 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout6)
            var optionLayout7 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout7)
            var optionLayout8 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout8)
            var optionLayout9 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout9)
            var optionLayout10 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout10)
            val scannerText : EditText = holder.itemView.findViewById(R.id.iv_scanner_text)
            val reScanBtn : ImageButton = holder.itemView.findViewById(R.id.btn_rescan)
            val chooseTagButton = holder.itemView.findViewById<FrameLayout>(R.id.chooseTag)
            val tagPickBtn = holder.itemView.findViewById<ImageButton>(R.id.tag_press)
            val questionBankType : Spinner = holder.itemView.findViewById(R.id.spinner_question_bank)
            val optionNums = listOf(
                optionNum2, optionNum3, optionNum4, optionNum5,
                optionNum6, optionNum7, optionNum8, optionNum9, optionNum10
            )
            val options = listOf(
                option1, option2, option3, option4, option5,
                option6, option7, option8, option9, option10
            )
            val checkBoxes = listOf(
                checkBox1, checkBox2, checkBox3, checkBox4, checkBox5,
                checkBox6, checkBox7, checkBox8, checkBox9, checkBox10
            )
            val checkBoxesForTureFalse = listOf(
                checkBoxForTrue,
                checkBoxForFalse
            )
            if(ConstantsOcrResults.questionList[position].questionType!=null){
                val index = ConstantsOcrResults.questionTypeList.indexOf(ConstantsOcrResults.questionList[position].questionType)
                if (index != -1) {
                    questionTypeSpinner.setSelection(index)
                }
            }
            questionTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position_spinner: Int,
                    id: Long
                ) {
                    scannerText.clearFocus()
                    when(position_spinner){
                        0 -> {
                            optionList.visibility = View.GONE
                            trueFalseLayout.visibility=View.GONE
                            checkBoxTint.visibility = View.GONE
                            isSingleChoice = false
                            isTrueFalse = false
                            checkBoxesForTureFalse.forEach { it.isChecked = false }
                            checkBoxes.forEach { it.isChecked = false}
                            options.forEach { it.setText("") }
                            ConstantsOcrResults.questionList[position].questionType ="Filling"
                        }
                        1 -> {
                            trueFalseLayout.visibility=View.GONE
                            checkBoxTint.visibility = View.VISIBLE
                            optionList.visibility = View.VISIBLE
                            isSingleChoice = true
                            isTrueFalse = false
                            checkBoxesForTureFalse.forEach { it.isChecked = false }
                            checkBoxes.forEachIndexed { index , it ->
                                it.isChecked = false
                                if(index == 0){
                                    it.visibility = View.VISIBLE
                                }else{
                                    it.visibility = View.GONE
                                }
                            }
                            options.forEachIndexed { index, editText ->
                                if (index == 0) {
                                    editText.visibility = View.VISIBLE
                                    editText.setText("")
                                } else {
                                    editText.visibility = View.GONE
                                    editText.setText("")
                                }
                            }
                            optionNums.forEach {
                                it.visibility = View.GONE
                            }
                            ConstantsOcrResults.questionList[position].questionType ="MultipleChoiceS"
                        }
                        2 -> {
                            trueFalseLayout.visibility=View.GONE
                            checkBoxTint.visibility = View.GONE
                            optionList.visibility = View.GONE
                            isSingleChoice = false
                            isTrueFalse = false
                            checkBoxesForTureFalse.forEach { it.isChecked = false }
                            checkBoxes.forEach { it.isChecked = false}
                            options.forEach { it.setText("") }
                            ConstantsOcrResults.questionList[position].questionType ="ShortAnswer"
                        }
                        3 -> {
                            trueFalseLayout.visibility=View.GONE
                            checkBoxTint.visibility = View.VISIBLE
                            optionList.visibility = View.VISIBLE
                            isSingleChoice = false
                            isTrueFalse = false
                            checkBoxesForTureFalse.forEach { it.isChecked = false }
                            checkBoxes.forEachIndexed { index , it ->
                                it.isChecked = false
                                if(index == 0){
                                    it.visibility = View.VISIBLE
                                }else{
                                    it.visibility = View.GONE
                                }
                            }

                            options.forEachIndexed { index, editText ->
                                if (index == 0) {
                                    editText.visibility = View.VISIBLE
                                    editText.setText("")
                                } else {
                                    editText.visibility = View.GONE
                                    editText.setText("")
                                }
                            }
                            optionNums.forEach {
                                it.visibility = View.GONE
                            }
                            ConstantsOcrResults.questionList[position].questionType ="MultipleChoiceM"
                        }
                        4 -> {
                            trueFalseLayout.visibility=View.VISIBLE
                            checkBoxTint.visibility = View.VISIBLE
                            optionList.visibility = View.GONE
                            isSingleChoice = false
                            isTrueFalse = true
                            checkBoxesForTureFalse.forEach { it.isChecked = false }
                            checkBoxes.forEach { it.isChecked = false}
                            options.forEach { it.setText("") }
                            ConstantsOcrResults.questionList[position].questionType ="TrueOrFalse"
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })
            //答案描述預設要有因此使用者沒填用預設
            ConstantsOcrResults.getOcrResult()[position].answerDescription = "目前為空"
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            //ocr 出來的文字
            val latexRenderView : ImageButton = holder.itemView.findViewById(R.id.btn_view)
            scannerText.setText(ConstantsOcrResults.getOcrResult()[position].description,TextView.BufferType.EDITABLE)

            scannerText.setOnTouchListener { view, event ->
                val editText = view as EditText
                val canScrollVertically = editText.canScrollVertically(1) || editText.canScrollVertically(-1)

                if (canScrollVertically) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return@setOnTouchListener false
            }
            scannerText.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if(scannerText.text.toString().isNotEmpty()){
                        ConstantsOcrResults.getOcrResult()[position].description = scannerText.text.toString()
                        latexRenderView.visibility = View.VISIBLE
                        reScanBtn.visibility = View.VISIBLE
                        btnScanPhoto.visibility = View.VISIBLE
                    }
                }else{
                    latexRenderView.visibility = View.GONE
                    reScanBtn.visibility = View.GONE
                    btnScanPhoto.visibility = View.GONE
                }
            }

            latexRenderView.setOnClickListener {
                val latexRenderDialog = Dialog(context)
                latexRenderDialog.setContentView(R.layout.dialog_review_latex)
                latexRenderDialog.setTitle("Latex Render")
                val mathView : com.qdot.mathrendererlib.MathRenderView = latexRenderDialog.findViewById(R.id.mathView)

                mathView.apply {
                    text = scannerText.text.toString()
                    textAlignment = TextAlign.START
                    textColor = "#000000"
                    mathBackgroundColor = "#FFFFFF"

                    setWebViewClient(object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            latexRenderDialog.show()
                        }
                    })
                }
            }

            reScanBtn.setOnClickListener {
                val builder =AlertDialog.Builder(activity,R.style.CustomAlertDialogStyle)
                    .setMessage(" 您確定要重新掃描嗎 ")
                    .setTitle("掃描結果")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    rescanPosition = position
                    val pictureDialog = AlertDialog.Builder(context)
                    pictureDialog.setTitle("Select Action")
                    val pictureDialogItems =
                        arrayOf("拍照","從相簿選擇")
                    pictureDialog.setItems(
                        pictureDialogItems
                    ) { dialog, which ->
                        when (which) {
                            // Here we have create the methods for image selection from GALLERY
                            0 -> activity.takePhotoToOcr( 1,onSuccess = { it1 -> notifyDataSetChanged() }, onFailure = { it1 -> })
                            1 -> activity.choosePhotoToOcr(1, onSuccess = { it1 ->notifyDataSetChanged()  }, onFailure = { it1 -> })
                        }
                    }
                    pictureDialog.show()
                }

                builder.setNegativeButton("取消") { dialog, which ->
                }
                builder.show()
            }
            //選擇標籤

            tagPickBtn.setOnClickListener {
                //用dialog去跳出標籤選單
                scannerText.clearFocus()
                val tagDialog = Dialog(context)
                tagDialog.setContentView(R.layout.dialog_choose_tag)
                //初始化所有的tag展示區域

                //這邊之後要放使用者常用的tags
                val list1 = ConstantsTag.getList1()

                var mTagContainerLayout1 = tagDialog.findViewById<TagContainerLayout>(R.id.tagcontainerLayout1)
                var mChooseTagContainerLayout1 = tagDialog.findViewById<TagContainerLayout>(R.id.chooseContainerLayout1)
                mTagContainerLayout1!!.setTags(list1)
                mChooseTagContainerLayout1.tags=tagQuestionList
                tagDialog.setTitle("選擇標籤")
                tagDialog.show()

                //給定對應的初始化物件

                mTagContainerLayout1.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
//                        tagBankList.add(text)
                        //當使用者選擇某個tag擇要顯示
//                        mChooseTagContainerLayout1.tags=tagBankList
                        mChooseTagContainerLayout1.tags=tagQuestionList
                        if(ConstantsOcrResults.getOcrResult()[position].tag?.contains(text)!=true){
                            tagQuestionList.add(text)
                            Log.e("tagtagtag","addaddadd ${ConstantsOcrResults.getOcrResult()[position].tag.toString()}")
                            ConstantsOcrResults.getOcrResult()[position].tag?.add(text) //將此tag記錄到等等要放進資料庫的題目的標籤列
                            mChooseTagContainerLayout1.tags=tagQuestionList
                        }

                    }

                    override fun onTagLongClick(position: Int, text: String) {
                        val dialog = android.app.AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                            .setTitle("long click")
                            .setMessage("You will delete this tag!")
                            .setPositiveButton("Delete") { dialog, which ->
                                if (position < mTagContainerLayout1.getChildCount()) {
                                    mTagContainerLayout1.removeTag(position)
                                }
                            }
                            .setNegativeButton(
                                "Cancel"
                            ) { dialog, which -> dialog.dismiss() }
                            .create()
                        dialog.show()
                    }

                    override fun onSelectedTagDrag(position: Int, text: String) {}
                    override fun onTagCrossClick(position: Int) {
                        //移除目前的tag
                        mTagContainerLayout1.removeTag(position);

                    }
                })

                mChooseTagContainerLayout1.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
                    }
                    override fun onTagLongClick(position: Int, text: String) {
                    }
                    override fun onSelectedTagDrag(position: Int, text: String) {}
                    override fun onTagCrossClick(tag_position: Int) {
                        tagQuestionList.remove(mChooseTagContainerLayout1.getTagText(tag_position))
                        ConstantsOcrResults.getOcrResult()[position].tag?.remove(mChooseTagContainerLayout1.getTagText(tag_position))
                        mChooseTagContainerLayout1.removeTag(tag_position)
                    }
                })
                val text = tagDialog.findViewById<View>(R.id.text_tag) as EditText
                val btnAddTag = tagDialog.findViewById<View>(R.id.btn_add_tag) as TextView
                btnAddTag.setOnClickListener {
                    //新增置常用的標籤列
                    mTagContainerLayout1.addTag(text.text.toString())
                }
                val enterButton : TextView = tagDialog.findViewById(R.id.enter_tag)
                val cancelButton : TextView = tagDialog.findViewById(R.id.cancel_tag)

                //按下確認新增結束tag
                enterButton.setOnClickListener {

                    holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags=tagQuestionList
                    tagDialog.dismiss()
                    Toast.makeText(context,"successful upload tag",Toast.LENGTH_SHORT).show()
                }

                cancelButton.setOnClickListener {
                    ConstantsOcrResults.getOcrResult()[position].tag?.clear()
                    ConstantsOcrResults.getOcrResult()[position].tag?.add("目前為空")
                    tagDialog.dismiss()
                }
            }
            chooseTagButton.setOnLongClickListener{
                //用dialog去跳出標籤選單
                scannerText.clearFocus()
                val tagDialog = Dialog(context)
                tagDialog.setContentView(R.layout.dialog_choose_tag)
                //初始化所有的tag展示區域
                var mTagContainerLayout1: TagContainerLayout? = null
                var mChooseTagContainerLayout1: TagContainerLayout? = null
                //這邊之後要放使用者常用的tags
                val list1 = ConstantsTag.getList1()

                mTagContainerLayout1 = tagDialog.findViewById(R.id.tagcontainerLayout1)
                mChooseTagContainerLayout1 = tagDialog.findViewById(R.id.chooseContainerLayout1)
                mTagContainerLayout1!!.setTags(list1)
                mChooseTagContainerLayout1.tags=tagQuestionList
                tagDialog.setTitle("選擇標籤")
                tagDialog.show()

                //給定對應的初始化物件

                mTagContainerLayout1.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
//                        tagBankList.add(text)
                        mChooseTagContainerLayout1.tags=tagQuestionList
                        if(ConstantsOcrResults.getOcrResult()[position].tag?.contains(text)!=true){
                            tagQuestionList.add(text)
                            Log.e("tagtagtag","addaddadd ${ConstantsOcrResults.getOcrResult()[position].tag.toString()}")
                            ConstantsOcrResults.getOcrResult()[position].tag?.add(text) //將此tag記錄到等等要放進資料庫的題目的標籤列
                            mChooseTagContainerLayout1.tags=tagQuestionList
                        }
                    }

                    override fun onTagLongClick(position: Int, text: String) {
                        val dialog = android.app.AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                            .setTitle("long click")
                            .setMessage("You will delete this tag!")
                            .setPositiveButton("Delete") { dialog, which ->
                                if (position < mTagContainerLayout1.getChildCount()) {
                                    mTagContainerLayout1.removeTag(position)
                                }
                            }
                            .setNegativeButton(
                                "Cancel"
                            ) { dialog, which -> dialog.dismiss() }
                            .create()
                        dialog.show()
                    }

                    override fun onSelectedTagDrag(position: Int, text: String) {}
                    override fun onTagCrossClick(position: Int) {
                        //移除目前的tag
                        mTagContainerLayout1.removeTag(position);

                    }
                })

                mChooseTagContainerLayout1.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
                    }
                    override fun onTagLongClick(position: Int, text: String) {
                    }
                    override fun onSelectedTagDrag(position: Int, text: String) {}
                    override fun onTagCrossClick(tag_position: Int) {
                        tagQuestionList.remove(mChooseTagContainerLayout1.getTagText(tag_position))
                        ConstantsOcrResults.getOcrResult()[position].tag?.remove(mChooseTagContainerLayout1.getTagText(tag_position))
                        mChooseTagContainerLayout1.removeTag(tag_position)
                    }
                })
                val text = tagDialog.findViewById<View>(R.id.text_tag) as EditText
                val btnAddTag = tagDialog.findViewById<View>(R.id.btn_add_tag) as TextView
                btnAddTag.setOnClickListener {
                    //新增置常用的標籤列
                    mTagContainerLayout1.addTag(text.text.toString())
                }
                val enterButton : TextView = tagDialog.findViewById(R.id.enter_tag)
                val cancelButton : TextView = tagDialog.findViewById(R.id.cancel_tag)

                //按下確認新增結束tag
                enterButton.setOnClickListener {

                    holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags=tagQuestionList
                    tagDialog.dismiss()
                    Toast.makeText(context,"successful upload tag",Toast.LENGTH_SHORT).show()
                }

                cancelButton.setOnClickListener {
                    ConstantsOcrResults.getOcrResult()[position].tag?.clear()
                    ConstantsOcrResults.getOcrResult()[position].tag?.add("目前為空")
                    tagDialog.dismiss()
                }
                true
            }

            val listenerForTrueFalse =
                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    scannerText.clearFocus()
                    ConstantsOcrResults.questionList[position].answerOptions?.clear()
                    if (isTrueFalse && isChecked) {
                        if (buttonView !== checkBoxForTrue) checkBoxForTrue.isChecked = false
                        if (buttonView !== checkBoxForFalse) checkBoxForFalse.isChecked = false
                        if(buttonView == checkBoxForTrue && checkBoxForTrue.isChecked) ConstantsOcrResults.questionList[position].answerOptions?.add("true")
                        if(buttonView == checkBoxForFalse && checkBoxForFalse.isChecked) ConstantsOcrResults.questionList[position].answerOptions?.add("false")
                    }
                }
            val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val index = checkBoxes.indexOf(buttonView)
                scannerText.clearFocus()
                if (index != -1) {
                    val optionText = options[index].text.toString().trim()

                    if (isSingleChoice && isChecked) {
                        checkBoxes.forEachIndexed { i, checkBox ->
                            if (i != index) checkBox.isChecked = false
                        }
                        ConstantsOcrResults.questionList[position].answerOptions?.clear()
                    }

                    val currentAnswerOptions = ConstantsOcrResults.questionList[position].answerOptions ?: ArrayList()

                    if (optionText.isNotEmpty()) {
                        if (isChecked && !currentAnswerOptions.contains(optionText)) {
                            currentAnswerOptions.add(optionText)
                        } else if (!isChecked) {
                            currentAnswerOptions.remove(optionText)
                        }
                    }else{
                        buttonView.isChecked = false
                        val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                            .setMessage(" 選項內容不能為空喔 ")
                            .setTitle("正確選項")
                            .setIcon(R.drawable.baseline_warning_amber_24)
                        builder.show()
                    }

                    ConstantsOcrResults.questionList[position].answerOptions = currentAnswerOptions
                }
            }
            checkBoxesForTureFalse.forEach { it.setOnCheckedChangeListener(listenerForTrueFalse) }
            checkBoxes.forEach { it.setOnCheckedChangeListener(listener) }
            //question bank type

            val handler = Handler(Looper.getMainLooper())
            val longPressRunnable = Runnable {
                chooseTagButton.performLongClick()
            }
            val  chooseTagOnAdapter = holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion)
            chooseTagOnAdapter.setOnTouchListener { _, event ->
                scannerText.clearFocus()
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // 檢查觸摸點是否在任何標籤上
                    for (i in 0 until chooseTagOnAdapter.childCount) {
                        val tagView = chooseTagOnAdapter.getChildAt(i)
                        val outRect = Rect()
                        tagView.getGlobalVisibleRect(outRect)
                        if (outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                            // 觸摸點在標籤上，不觸發FrameLayout的點擊事件
                            return@setOnTouchListener false
                        }
                    }
                    // 觸摸點在空白區域，將事件交給FrameLayout處理
                    handler.postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
                } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    handler.removeCallbacks(longPressRunnable)
                }
                false
            }
            chooseTagOnAdapter.setOnTagClickListener(object : TagView.OnTagClickListener {
                override fun onTagClick(tag_position: Int, text: String) {
                }
                override fun onTagLongClick(tag_position: Int, text: String) {
                }
                override fun onSelectedTagDrag(tag_position: Int, text: String) {}
                override fun onTagCrossClick(tag_position: Int) {
                    tagQuestionList.remove(chooseTagOnAdapter.getTagText(tag_position))
                    ConstantsOcrResults.getOcrResult()[position].tag?.remove(chooseTagOnAdapter.getTagText(tag_position))
                    chooseTagOnAdapter.removeTag(tag_position)
                    if(ConstantsOcrResults.getOcrResult()[position].tag?.size == 0){
                        ConstantsOcrResults.getOcrResult()[position].tag?.add("目前為空喔")
                    }
                    if(chooseTagOnAdapter.tags.size == 0) chooseTagOnAdapter.tags = ConstantsTag.getEmptyList()
                }
            })
            //題庫列表的下拉式選單
            val bankTypeList : ArrayList<String> = ArrayList()
            val hintType : String = "請選擇題庫 *"
            bankTypeList.add(hintType)
            if(ConstantsQuestionBankFunction.allBanksReturnResponse!=null){
                for(i in ConstantsQuestionBankFunction.allBanksReturnResponse!!.questionBanks) bankTypeList.add(i.title)
            }
            questionBankType.adapter =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, bankTypeList)

            //btn_scan_submit
            val btnAddAnswer : TextView = holder.itemView.findViewById(R.id.btn_add_answer)
            if((ConstantsOcrResults.questionList[position].answerDescription?.isNotEmpty() == true && !ConstantsOcrResults.questionList[position].answerDescription.equals("目前為空"))||ConstantsOcrResults.questionList[position].answerImages?.isNotEmpty() == true){
                btnAddAnswer.text = "查看答案 ✅"
            }
            btnAddAnswer.setOnClickListener {
                //新增答案
                scannerText.clearFocus()
                val answerDialog = Dialog(context)
                answerDialog.setContentView(R.layout.dialog_create_answer)
                answerDialog.setTitle("新增答案")


                //答案描述
                val answerDescription: EditText = answerDialog.findViewById(R.id.iv_answer_description_text)!! //答案描述
                val showImage : ImageView = answerDialog.findViewById(R.id.iv_answer_image)

                if(ConstantsOcrResults.questionList[position].answerDescription?.isNotEmpty() == true){
                    answerDescription.setText(ConstantsOcrResults.questionList[position].answerDescription)
                }

                if(ConstantsOcrResults.questionList[position].answerImages?.isNotEmpty()==true){
                   showImage.setImageBitmap(ConstantsOcrResults.questionList[position].answerImages?.get(0)
                      ?.let { it1 -> base64ToBitmap(it1) })
                }

                //答案描述的圖片
                val answerChoosePhoto: ImageButton = answerDialog.findViewById(R.id.answer_choose_photo)!! //選擇圖片 iv_answer_image用於顯示圖片
                answerChoosePhoto.setOnClickListener(View.OnClickListener {
                    //TODO
                    //iv_answer_image
                    var selectBitmap : Bitmap?= null
                    ConstantsDialogFunction.dialogChoosePhotoFromGallery(activity) {
                        bitmap ->
                            if(bitmap!=null){
                                selectBitmap = bitmap
                                selectBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
                                showImage.setImageBitmap(selectBitmap)
                                val selectPhotoBase64String : String = ConstantsFunction.encodeImage(selectBitmap!!)!!
                                ConstantsOcrResults.questionList[position].answerImages?.clear()
                                ConstantsOcrResults.questionList[position].answerImages?.add(selectPhotoBase64String)
                                val actionDialog = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                                actionDialog.setTitle("是否答案圖片要OCR")
                                val actionDialogItems =
                                    arrayOf("要", "不要")
                                actionDialog.setItems(
                                    actionDialogItems
                                ) { dialog, which ->
                                    when (which) {
                                        // Here we have create the methods for image selection from GALLERY
                                        0 -> {
                                            activity.showProgressDialog("目前正在處理答案圖片OCR")
                                            ConstantsScanServiceFunction.scanBase64ToOcrText(
                                                selectPhotoBase64String!!,
                                                activity,0,onSuccess = { it1 ->
                                                    activity.hideProgressDialog()
                                                    answerDescription.setText(it1)
                                                }, onFailure = { it1 ->
                                                    activity.hideProgressDialog()
                                                    activity.showErrorSnackBar("掃描錯誤")
                                                }
                                            )

                                        }
                                        1 -> Log.e("not answer image to ocr","true")
                                    }
                                }
                                actionDialog.show()

                            }else{
                                Toast.makeText(context," choosePhoto has error",Toast.LENGTH_SHORT).show()
                            }
                    }

                    Log.e("in answer pic",selectBitmap.toString())

                })

                //取消本次新增答案
                val answerCancel : TextView = answerDialog.findViewById(R.id.answer_cancel)
                answerCancel.setOnClickListener(View.OnClickListener {
                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您確定要取消所有目前的新增結果嗎 ")
                        .setTitle("取消新增答案")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.setPositiveButton("確認") { dialog, which ->
                        btnAddAnswer.text = "答案描述 \uD83D\uDCA1"
                        answerDialog.dismiss()
                        ConstantsOcrResults.questionList[position].answerImages?.clear()
                        ConstantsOcrResults.questionList[position].answerDescription = ""
                    }
                    builder.setNegativeButton("取消") { dialog, which ->
                    }
                    builder.show()
                })

                //確認新增答案
                val answerEnter: TextView = answerDialog.findViewById(R.id.answer_enter)!!
                answerEnter.setOnClickListener(View.OnClickListener {
                    //TODO 將結果記錄至常數字串
                    btnAddAnswer.text = "查看答案 ✅"
                    if(answerDescription.text.toString()!=""){
                        ConstantsOcrResults.questionList[position].answerDescription = answerDescription.text.toString()
                    }
                    answerDialog.dismiss()
                })
                answerDialog.show()
            }

            //新增目前題目描述的圖片

            btnScanPhoto.setOnClickListener {
                scannerText.clearFocus()
                val imageDialog = Dialog(context)
                imageDialog.setContentView(R.layout.dialog_create_image)
                imageDialog.setTitle("新增圖片")
                var imageCount : Int = imageList.size //TODO 先暫時做只有一張的
                val showImage : ImageView = imageDialog.findViewById(R.id.iv_answer_image0)
                val showImage1 : ImageView = imageDialog.findViewById(R.id.iv_answer_image1)
                val showImage2 : ImageView = imageDialog.findViewById(R.id.iv_answer_image2)
                if(ConstantsOcrResults.questionList[position].image?.isNotEmpty()==true){
                    showImage.setImageBitmap(ConstantsOcrResults.questionList[position].image?.get(0)
                        ?.let { it1 -> base64ToBitmap(it1) })
                }
                if(ConstantsOcrResults.questionList[position].image?.size == 2){
                    showImage1.setImageBitmap(ConstantsOcrResults.questionList[position].image?.get(1)
                        ?.let { it1 -> base64ToBitmap(it1) })
                    showImage1.visibility = View.VISIBLE
                }
                if(ConstantsOcrResults.questionList[position].image?.size == 3){
                    showImage1.setImageBitmap(ConstantsOcrResults.questionList[position].image?.get(1)
                        ?.let { it1 -> base64ToBitmap(it1) })
                    showImage1.visibility = View.VISIBLE
                    showImage2.setImageBitmap(ConstantsOcrResults.questionList[position].image?.get(2)
                        ?.let { it1 -> base64ToBitmap(it1) })
                    showImage2.visibility = View.VISIBLE
                }
                val createPhoto: TextView = imageDialog.findViewById(R.id.answer_choose_image)!! //新增圖片(最多三張) iv_answer_image0 iv_answer_image1 iv_answer_image2
                createPhoto.setOnClickListener(View.OnClickListener {
                    var selectBitmap : Bitmap?= null
                    if(imageCount < 3){
                        ConstantsDialogFunction.dialogChoosePhotoFromGallery(activity) {
                                bitmap ->
                            if(bitmap!=null){
                                selectBitmap = bitmap
                                selectBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
                                Toast.makeText(context," success choose photo",Toast.LENGTH_SHORT).show()
                                //顯示目前已新增的圖片
                                if(imageCount == 0){
                                    showImage.setImageBitmap(selectBitmap)
                                }else if (imageCount == 1){
                                    showImage1.setImageBitmap(selectBitmap)
                                    showImage1.visibility = View.VISIBLE
                                }else if(imageCount == 2){
                                    showImage2.setImageBitmap(selectBitmap)
                                    showImage2.visibility = View.VISIBLE
                                }

                                val selectPhotoBase64String : String = ConstantsFunction.encodeImage(selectBitmap!!)!!
                                imageList.add(selectPhotoBase64String)
                                ConstantsOcrResults.questionList[position].image = imageList
                                imageCount +=1
                            }else{
                                Toast.makeText(context," choosePhoto has error",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    Log.e("in answer pic",selectBitmap.toString())
                })
                val createImageCancel : TextView = imageDialog.findViewById(R.id.answer_image_cancel)
                createImageCancel.setOnClickListener(View.OnClickListener {
                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您確定要取消所有目前的新增圖片嗎 ")
                        .setTitle("取消新增圖片")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.setPositiveButton("確認") { dialog, which ->
                        ConstantsOcrResults.questionList[position].image?.clear()
                        btnScanPhoto.setImageResource(R.drawable.addimage)
                        imageList.clear()
                        imageDialog.dismiss()
                    }
                    builder.setNegativeButton("取消") { dialog, which ->
                        imageDialog.dismiss()
                    }
                    builder.show()
                })
                val createImageEnter : TextView = imageDialog.findViewById(R.id.answer_image_enter)
                createImageEnter.setOnClickListener {

                    btnScanPhoto.setImageResource(R.drawable.afteraddimage)
                    //TODO 將結果紀錄至常數字串的position
                    imageDialog.dismiss()
                }
                imageDialog.show()
            }

            val optionLayouts = listOf(
                optionLayout2,optionLayout3,optionLayout4,optionLayout5,optionLayout6,optionLayout7,optionLayout8,optionLayout9,optionLayout10

            )
            //超過四個選項因此點及新增選項
            val addOptionsButton : TextView = holder.itemView.findViewById(R.id.add_options_button)
            val removeOptionsButton : TextView = holder.itemView.findViewById(R.id.minus_options_button)
            val optionsBtn = listOf(
                option2, option3, option4, option5, option6, option7, option8, option9, option10
            )

            val checkBoxesBtn = listOf(
                checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10
            )
            addOptionsButton.setOnClickListener {
                scannerText.clearFocus()
                if (optionsNum < 10) {
                    optionsNum += 1
                    optionLayouts[optionsNum - 2].visibility = View.VISIBLE
                    optionsBtn[optionsNum - 2].visibility = View.VISIBLE
                    checkBoxesBtn[optionsNum - 2].visibility = View.VISIBLE
                    optionNums[optionsNum-2].visibility = View.VISIBLE
                } else {
                    Toast.makeText(context, "已達最多的選項限制了喔", Toast.LENGTH_SHORT).show()
                }
            }

            removeOptionsButton.setOnClickListener {
                scannerText.clearFocus()
                if (optionsNum > 1) {
                    optionsNum -= 1
                    optionLayouts[optionsNum - 1].visibility = View.GONE
                    optionsBtn[optionsNum - 1].visibility = View.GONE
                    checkBoxesBtn[optionsNum - 1].visibility = View.GONE
                    optionNums[optionsNum-1].visibility = View.GONE
                    optionsBtn[optionsNum - 1].setText("")
                    checkBoxesBtn[optionsNum - 1].isChecked = false
                } else {
                    Toast.makeText(context, "已達不能再少了喔", Toast.LENGTH_SHORT).show()
                }
            }
            //掃描結果新增置資料庫
            val btnScanSubmit : TextView  = holder.itemView.findViewById(R.id.btn_scan_submit)
            btnScanSubmit.setOnClickListener {
                //TODO 寫進資料庫
                val options: ArrayList<String> = ArrayList()
                val editTextList = listOf(
                    option1, option2, option3, option4, option5,
                    option6, option7, option8, option9, option10
                )
                val bankIndex: Int = questionBankType.selectedItemPosition
                var qb_id : String = ""
                if(ConstantsQuestionBankFunction.allBanksReturnResponse?.questionBanks!=null){
                    for(i in ConstantsQuestionBankFunction.allBanksReturnResponse?.questionBanks!!){
                        if(i.title == bankTypeList[bankIndex]){
                            qb_id = i._id
                        }
                    }
                }

                if(imageList.isNotEmpty()){
                    ConstantsOcrResults.questionList[position].image = imageList
                }
                for (editText in editTextList) {
                    val optionText = editText.text.toString().trim()
                    if (optionText.isNotEmpty()) {
                        options.add(optionText)
                    }
                }
                val questionTypeOption : Spinner = holder.itemView.findViewById(R.id.spinner_question_type)
                val questionTypeIndex : Int = questionTypeOption.selectedItemPosition
                ConstantsOcrResults.questionList[position].questionBank= qb_id
                ConstantsOcrResults.questionList[position].description = scannerText.text.toString()
                ConstantsOcrResults.questionList[position].title = title.text.toString()
                ConstantsOcrResults.questionList[position].number = questionNum.text.toString()
                //判斷題號是否為純數字
                val isNumber = questionNum.text.toString().matches(Regex("\\d*"))

                ConstantsOcrResults.questionList[position].options = options
                ConstantsOcrResults.questionList[position].questionType =ConstantsOcrResults.questionTypeList[questionTypeIndex]
                if(questionTypeIndex == 4){
                    ConstantsOcrResults.questionList[position].options?.clear()
                    ConstantsOcrResults.questionList[position].options?.add("true")
                    ConstantsOcrResults.questionList[position].options?.add("false")
                    Log.e("opitons    q",ConstantsOcrResults.questionList[position].options.toString())
                }

                Log.e("post question",ConstantsOcrResults.questionList[position].toString())

                if(ConstantsOcrResults.questionList[position].answerDescription==""){
                    ConstantsOcrResults.questionList[position].answerDescription = "目前沒有答案描述"
                }

                //判斷是否符合格式符合才能新增
                if(ConstantsOcrResults.questionList[position].title==""){
                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您的標題(title)不能為空喔 ")
                        .setTitle("確認標題")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()

                }else if(ConstantsOcrResults.questionList[position].number==""){
                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您的題號(number)不能為空喔 ")
                        .setTitle("確認題號")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                }else if(!isNumber){
                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您的題號需要為數字喔 ")
                        .setTitle("錯誤題號")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                }else if((questionTypeIndex==1||questionTypeIndex==3)&&ConstantsOcrResults.questionList[position].options?.size==0){

                    val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 您的題目選項(options)不能為空喔 ")
                        .setTitle("題目選項")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()

                    if(ConstantsOcrResults.questionList[position].answerOptions?.size == 0){
                        ConstantsOcrResults.questionList[position].answerOptions?.add("目前答案選項為空")
                    }
                }else if(ConstantsOcrResults.questionList[position].questionBank=="") {
                    val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                        .setMessage(" 選擇放入的題庫不能為空如果目前無可選擇的請先新增題庫喔 ")
                        .setTitle("選擇放入題庫")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                }else{
                    ConstantsQuestionFunction.postQuestionPosition=position
                    activity.showProgressDialog("新增中請稍等")
                    ConstantsQuestionFunction.postQuestion( ConstantsOcrResults.questionList[position],activity,
                        onSuccess = {
                            activity.hideProgressDialog()
                            val intent = Intent(activity, ScannerTextWorkSpaceActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        },
                        onFailure = {
                            it -> Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                            activity.hideProgressDialog()
                        }
                        )
                }

            }
            val btnScanCancel : TextView  = holder.itemView.findViewById(R.id.btn_scan_cancel)
            btnScanCancel.setOnClickListener {
                //TODO 取消新增
                val builder =AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
                    .setMessage(" 您確定要取消這次的掃描結果嗎 ")
                    .setTitle("取消掃描結果")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    ConstantsOcrResults.questionList.removeAt(position)
                    notifyDataSetChanged()
                    val intent = Intent(activity, ScannerTextWorkSpaceActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
                builder.setNegativeButton("取消") { dialog, which ->
                }
                builder.show()
            }


        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int, model: ScanQuestionModel)
    }
    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}