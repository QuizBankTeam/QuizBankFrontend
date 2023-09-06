package com.example.quizbanktest.adapters.scan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
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
import com.example.quizbanktest.utils.*
import java.io.ByteArrayOutputStream


class OcrResultViewAdapter(
    private val activity: BaseActivity,
    private val context: Context,
    private var list: ArrayList<QuestionModel>,
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
        val tagQuestionList:ArrayList<String> = ArrayList()//顯示有哪些題目標籤
        val imageList : ArrayList<String> = ArrayList() //目前選擇了那些圖片(包括 題目描述跟答案之圖片)
        val out = ByteArrayOutputStream()
        var optionsNum : Int = 1 //預設選項為四個
        val model = list[position] //知道目前是哪個東西被選擇
        //去給他初始化
        holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags = ConstantsTag.getEmptyList()

        //題目的標題
        val title : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_title)
        var isSingleChoice = false
        //題目的題號
        val questionNum : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_num)
        val questionTypeSpinner : Spinner = holder.itemView.findViewById(R.id.spinner_question_type)
        var mAdapter = QuestionTypeAdapter(activity, ConstantsOcrResults.questionTypeSpinner)
        questionTypeSpinner.setAdapter(mAdapter)
        questionTypeSpinner.setSelection(0, false)

        if (holder is MyViewHolder) {
            //一題最多只能有十個選項
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
            questionTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    when(position){
                        0 -> {
                            optionList.visibility = View.GONE
                            isSingleChoice = false
                        }
                        1 -> {
                            optionList.visibility = View.VISIBLE
                            isSingleChoice = true
                        }
                        2 -> {
                            optionList.visibility = View.GONE
                            isSingleChoice = false
                        }
                        3 -> {
                            optionList.visibility = View.VISIBLE
                            isSingleChoice = false
                        }
                        4 -> {
                            optionList.visibility = View.GONE
                            isSingleChoice = false
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
            val scannerText : EditText = holder.itemView.findViewById(R.id.iv_scanner_text)
            scannerText.setText(ConstantsOcrResults.getOcrResult()[position].description,TextView.BufferType.EDITABLE)


            val reScanBtn : ImageButton = holder.itemView.findViewById(R.id.btn_rescan)
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
            val chooseTagButton = holder.itemView.findViewById<FrameLayout>(R.id.chooseTag)
            val tagPickBtn = holder.itemView.findViewById<ImageButton>(R.id.tag_press)
            tagPickBtn.setOnClickListener {
                //用dialog去跳出標籤選單
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
                        tagQuestionList.add(text)
                        //當使用者選擇某個tag擇要顯示
//                        mChooseTagContainerLayout1.tags=tagBankList
                        mChooseTagContainerLayout1.tags=tagQuestionList
                        ConstantsOcrResults.getOcrResult()[position].tag?.add(text) //將此tag記錄到等等要放進資料庫的題目的標籤列
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
                    tagDialog.dismiss()
                }
            }
            chooseTagButton.setOnLongClickListener{
                //用dialog去跳出標籤選單
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
                        tagQuestionList.add(text)
                        //當使用者選擇某個tag擇要顯示
//                        mChooseTagContainerLayout1.tags=tagBankList
                        mChooseTagContainerLayout1.tags=tagQuestionList
                        ConstantsOcrResults.getOcrResult()[position].tag?.add(text) //將此tag記錄到等等要放進資料庫的題目的標籤列
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
                    tagDialog.dismiss()
                }
                true
            }
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
            val listener =
                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if (isSingleChoice && isChecked) {
                        if (buttonView !== checkBox1) checkBox1.isChecked = false
                        if (buttonView !== checkBox2) checkBox2.isChecked = false
                        if (buttonView !== checkBox3) checkBox3.isChecked = false
                        if (buttonView !== checkBox4) checkBox4.isChecked = false
                        if (buttonView !== checkBox5) checkBox5.isChecked = false
                        if (buttonView !== checkBox6) checkBox6.isChecked = false
                        if (buttonView !== checkBox7) checkBox7.isChecked = false
                        if (buttonView !== checkBox8) checkBox8.isChecked = false
                        if (buttonView !== checkBox9) checkBox9.isChecked = false
                        if (buttonView !== checkBox10) checkBox10.isChecked = false
                    }
                }
            checkBox1.setOnCheckedChangeListener(listener)
            checkBox2.setOnCheckedChangeListener(listener)
            checkBox3.setOnCheckedChangeListener(listener)
            checkBox4.setOnCheckedChangeListener(listener)
            checkBox5.setOnCheckedChangeListener(listener)
            checkBox6.setOnCheckedChangeListener(listener)
            checkBox7.setOnCheckedChangeListener(listener)
            checkBox8.setOnCheckedChangeListener(listener)
            checkBox9.setOnCheckedChangeListener(listener)
            checkBox10.setOnCheckedChangeListener(listener)

            //question bank type
            val questionBankType : Spinner = holder.itemView.findViewById(R.id.spinner_question_bank)
            val handler = Handler(Looper.getMainLooper())
            val longPressRunnable = Runnable {
                chooseTagButton.performLongClick()
            }
            val  chooseTagOnAdapter = holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion)
            chooseTagOnAdapter.setOnTouchListener { _, event ->
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
            btnAddAnswer.setOnClickListener {
                //新增答案
                val answerDialog = Dialog(context)
                answerDialog.setContentView(R.layout.dialog_create_answer)
                answerDialog.setTitle("新增答案")


                //答案描述
                val answerDescription: EditText = answerDialog.findViewById(R.id.iv_answer_description_text)!! //答案描述

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
                                val showImage : ImageView = answerDialog.findViewById(R.id.iv_answer_image)
                                showImage.setImageBitmap(selectBitmap)
                                val selectPhotoBase64String : String = ConstantsFunction.encodeImage(selectBitmap!!)!!
                                ConstantsOcrResults.questionList[position].image?.add(selectPhotoBase64String)
                                imageList.add(selectPhotoBase64String)
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
            val btnScanPhoto : ImageButton  = holder.itemView.findViewById(R.id.btn_scan_photo)
            btnScanPhoto.setOnClickListener {

                val imageDialog = Dialog(context)
                imageDialog.setContentView(R.layout.dialog_create_image)
                imageDialog.setTitle("新增圖片")
                var imageCount : Int = 1 //TODO 先暫時做只有一張的
                val createPhoto: TextView = imageDialog.findViewById(R.id.answer_choose_image)!! //新增圖片(最多三張) iv_answer_image0 iv_answer_image1 iv_answer_image2
                createPhoto.setOnClickListener(View.OnClickListener {
                    var selectBitmap : Bitmap?= null


                    ConstantsDialogFunction.dialogChoosePhotoFromGallery(activity) {
                            bitmap ->
                        if(bitmap!=null){
                            selectBitmap = bitmap
                            selectBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
                            Toast.makeText(context," success choose photo",Toast.LENGTH_SHORT).show()
                            //顯示目前已新增的圖片
                            val showImage : ImageView = imageDialog.findViewById(R.id.iv_answer_image0)
                            showImage.setImageBitmap(selectBitmap)
                            val selectPhotoBase64String : String = ConstantsFunction.encodeImage(selectBitmap!!)!!
                            imageList.add(selectPhotoBase64String)
                            imageCount +=1
                        }else{
                            Toast.makeText(context," choosePhoto has error",Toast.LENGTH_SHORT).show()
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
                        btnAddAnswer.text = "新增圖片   ⬆"
                        imageDialog.dismiss()
                    }
                    builder.setNegativeButton("取消") { dialog, which ->
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
            var optionLayout2 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout2)
            var optionLayout3 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout3)
            var optionLayout4 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout4)
            var optionLayout5 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout5)
            var optionLayout6 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout6)
            var optionLayout7 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout7)
            var optionLayout8 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout8)
            var optionLayout9 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout9)
            var optionLayout10 : LinearLayout = holder.itemView.findViewById(R.id.optionLayout10)
            //超過四個選項因此點及新增選項
            val addOptionsButton : TextView = holder.itemView.findViewById(R.id.add_options_button)
            val removeOptionsButton : TextView = holder.itemView.findViewById(R.id.minus_options_button)
            addOptionsButton.setOnClickListener{
                if(optionsNum == 10){ //不能超過十個
                    Toast.makeText(context,"已達最多的選項限制了喔",Toast.LENGTH_SHORT).show()
                }else{
                    optionsNum += 1
                }
                when (optionsNum) {
                    2 -> {
                        removeOptionsButton.visibility = View.VISIBLE
                        option2.visibility = View.VISIBLE
                        optionLayout2.visibility = View.VISIBLE
                        checkBox2.visibility = View.VISIBLE
                    }
                    3 -> {
                        option3.visibility = View.VISIBLE
                        optionLayout3.visibility = View.VISIBLE
                        checkBox3.visibility = View.VISIBLE
                    }
                    4-> {
                        option4.visibility = View.VISIBLE
                        optionLayout4.visibility = View.VISIBLE
                        checkBox4.visibility = View.VISIBLE
                    }
                    5 -> {
                        option5.visibility = View.VISIBLE
                        optionLayout5.visibility = View.VISIBLE
                        checkBox5.visibility = View.VISIBLE
                    }
                    6 -> {
                        option6.visibility = View.VISIBLE
                        optionLayout6.visibility = View.VISIBLE
                        checkBox6.visibility = View.VISIBLE
                    }
                    7-> {
                        option7.visibility = View.VISIBLE
                        optionLayout7.visibility = View.VISIBLE
                        checkBox7.visibility = View.VISIBLE
                    }
                    8-> {
                        option8.visibility = View.VISIBLE
                        optionLayout8.visibility = View.VISIBLE
                        checkBox8.visibility = View.VISIBLE
                    }
                    9-> {
                        option9.visibility = View.VISIBLE
                        optionLayout9.visibility = View.VISIBLE
                        checkBox9.visibility = View.VISIBLE
                    }
                    10-> {
                        option10.visibility = View.VISIBLE
                        optionLayout10.visibility = View.VISIBLE
                        checkBox10.visibility = View.VISIBLE
                    }
                }

            }

            removeOptionsButton.setOnClickListener{
                if(optionsNum == 1){ //不能超過十個
                    Toast.makeText(context,"已達不能再少了喔",Toast.LENGTH_SHORT).show()
                }else{
                    optionsNum -= 1
                }
                when (optionsNum+1) {
                    2 -> {
                        option2.visibility = View.GONE
                        optionLayout2.visibility = View.GONE
                        checkBox2.visibility = View.GONE
                    }
                    3 -> {
                        option3.visibility = View.GONE
                        checkBox3.visibility = View.GONE
                        optionLayout3.visibility = View.GONE
                    }
                    4-> {
                        option4.visibility = View.GONE
                        checkBox4.visibility = View.GONE
                        optionLayout4.visibility = View.GONE
                    }
                    5-> {
                        option5.visibility = View.GONE
                        checkBox5.visibility = View.GONE
                        optionLayout5.visibility = View.GONE
                    }
                    6 -> {
                        option6.visibility = View.GONE
                        checkBox6.visibility = View.GONE
                        optionLayout6.visibility = View.GONE
                    }
                    7-> {
                        option7.visibility = View.GONE
                        checkBox7.visibility = View.GONE
                        optionLayout7.visibility = View.GONE
                    }
                    8-> {
                        option8.visibility = View.GONE
                        checkBox8.visibility = View.GONE
                        optionLayout8.visibility = View.GONE
                    }
                    9-> {
                        option9.visibility = View.GONE
                        checkBox9.visibility = View.GONE
                        optionLayout9.visibility = View.GONE
                    }
                    10-> {
                        option10.visibility = View.GONE
                        checkBox10.visibility = View.GONE
                        optionLayout10.visibility = View.GONE
                    }
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
                            notifyDataSetChanged()
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
        fun onClick(position: Int, model: QuestionModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}