package com.example.quizbanktest.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import co.lujun.androidtagview.TagView
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.introducemyself.utils.ConstantsTag
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.activity.TagActivity
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.*
import java.io.ByteArrayOutputStream
import java.lang.Boolean


class OcrResultViewAdapter(
    private val activity: BaseActivity,
    private val context: Context,
    private var list: ArrayList<QuestionModel>
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val tagBankList:ArrayList<String> = ArrayList()
        val tagRelateList:ArrayList<String> = ArrayList()
        val tagRangeList:ArrayList<String> = ArrayList()
        val imageList : ArrayList<String> = ArrayList()
        val out = ByteArrayOutputStream()
        var optionsNum : Int = 4
        val model = list[position]
        holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForBank).tags = ConstantsTag.getList1()
        holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags = ConstantsTag.getList2()
        holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForRange).tags = ConstantsTag.getList3()
        val title : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_title)
        val questionNum : EditText = holder.itemView.findViewById(R.id.iv_ocr_question_num)

        if (holder is MyViewHolder) {
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

            ConstantsOcrResults.getOcrResult()[position].answerDescription = "目前為空"
            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            val scannerText : EditText = holder.itemView.findViewById(R.id.iv_scanner_text)
            scannerText.setText(ConstantsOcrResults.getOcrResult()[position].description,TextView.BufferType.EDITABLE)


            //選擇標籤
            val chooseTagButton = holder.itemView.findViewById<LinearLayout>(R.id.chooseTag)
            chooseTagButton.setOnClickListener{
                val tagDialog = Dialog(context)
                tagDialog.setContentView(R.layout.dialog_choose_tag)
                var mTagContainerLayout1: TagContainerLayout? = null
                var mTagContainerLayout2: TagContainerLayout? = null
                var mTagContainerLayout3: TagContainerLayout? = null
                val list1 = ConstantsTag.getList1()
                val list2 = ConstantsTag.getList2()
                val list3 = ConstantsTag.getList3()
                tagDialog.setTitle("選擇標籤")
                tagDialog.show()
                mTagContainerLayout1 = tagDialog.findViewById(R.id.tagcontainerLayout1)
                mTagContainerLayout2 = tagDialog.findViewById(R.id.tagcontainerLayout2)
                mTagContainerLayout3 = tagDialog.findViewById(R.id.tagcontainerLayout3)
                mTagContainerLayout1!!.setTags(list1)
                mTagContainerLayout2!!.setTags(list2)
                mTagContainerLayout3!!.setTags(list3)

                mTagContainerLayout1.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
                        tagBankList.add(text)
                        ConstantsOcrResults.getOcrResult()[position].tag?.add(text)
                        Toast.makeText(
                            activity, "click-position:$tag_position, text:$text",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTagLongClick(position: Int, text: String) {
                        val dialog = android.app.AlertDialog.Builder(activity)
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
                        mTagContainerLayout1.removeTag(position);
                        Toast.makeText(
                            activity, "Click TagView cross! position = $position",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

                mTagContainerLayout2.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
                        tagRelateList.add(text)
                        ConstantsOcrResults.getOcrResult()[position].tag?.add(text)
                        Toast.makeText(
                            activity, "click-position:$tag_position, text:$text",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onTagLongClick(position: Int, text: String) {
                        val dialog = android.app.AlertDialog.Builder(activity)
                            .setTitle("long click")
                            .setMessage("You will delete this tag!")
                            .setPositiveButton("Delete") { dialog, which ->
                                if (position < mTagContainerLayout2.getChildCount()) {
                                    mTagContainerLayout2.removeTag(position)
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
                        mTagContainerLayout2.removeTag(position)
                        Toast.makeText(
                            activity, "Click TagView cross! position = $position",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                mTagContainerLayout3.setOnTagClickListener(object : TagView.OnTagClickListener {
                    override fun onTagClick(tag_position: Int, text: String) {
                        tagRangeList.add(text)
                        ConstantsOcrResults.getOcrResult()[position].tag?.add(text)
                        val selectedPositions = mTagContainerLayout3.getSelectedTagViewPositions()
                        //deselect all tags when click on an unselected tag. Otherwise show toast.
                        if (selectedPositions.isEmpty() || selectedPositions.contains(position)) {
                            Toast.makeText(
                                activity, "click-position:$tag_position, text:$text",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            //deselect all tags
                            for (i in selectedPositions) {
                                mTagContainerLayout3.deselectTagView(i)
                            }
                        }
                    }

                    override fun onTagLongClick(position: Int, text: String) {
                        mTagContainerLayout3.toggleSelectTagView(position)
                        val selectedPositions = mTagContainerLayout3.getSelectedTagViewPositions()
                        Toast.makeText(
                            activity, "selected-positions:$selectedPositions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSelectedTagDrag(position: Int, text: String) {
                        val clip = ClipData.newPlainText("Text", text)
                        val view: View = mTagContainerLayout3.getTagView(position)
                        val shadow = View.DragShadowBuilder(view)
                        view.startDrag(clip, shadow, Boolean.TRUE, 0)
                    }

                    override fun onTagCrossClick(position: Int) {
                        mTagContainerLayout3.removeTag(position)
                    }
                })

                val text = tagDialog.findViewById<View>(R.id.text_tag) as EditText
                val btnAddTag = tagDialog.findViewById<View>(R.id.btn_add_tag) as TextView
                btnAddTag.setOnClickListener {
                    mTagContainerLayout1.addTag(text.text.toString())

                }

                val text_relate = tagDialog.findViewById<View>(R.id.text_tag_relate) as EditText
                val btnAddTag_relate = tagDialog.findViewById<View>(R.id.btn_add_tag_relate) as TextView
                btnAddTag_relate.setOnClickListener {
                    mTagContainerLayout2.addTag(text_relate.text.toString())

                }

                val text_range = tagDialog.findViewById<View>(R.id.text_tag_range) as EditText
                val btnAddTag_range = tagDialog.findViewById<View>(R.id.btn_add_tag_range) as TextView
                btnAddTag_range.setOnClickListener {
                    mTagContainerLayout3.addTag(text_range.text.toString())

                }

                val enterButton : TextView = tagDialog.findViewById(R.id.enter_tag)
                val cancelButton : TextView = tagDialog.findViewById(R.id.cancel_tag)

                enterButton.setOnClickListener {
                    holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForBank).tags=tagBankList
                    holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags=tagRelateList
                    holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForRange).tags=tagRangeList
                    tagDialog.dismiss()
                    Toast.makeText(activity,"successful upload tag",Toast.LENGTH_SHORT).show()
                }

                cancelButton.setOnClickListener {
                    ConstantsOcrResults.getOcrResult()[position].tag?.clear()
                    tagDialog.dismiss()
                }
            }

            //question bank type
            val questionBankType : Spinner = holder.itemView.findViewById(R.id.spinner_question_bank)
            val bankTypeList : ArrayList<String> = ArrayList()
            val hintType : String = "請選擇將題目新增至下列題庫"
            bankTypeList.add(hintType)
            if(ConstantsQuestionBankFunction.allBanksReturnResponse!=null){
                for(i in ConstantsQuestionBankFunction.allBanksReturnResponse!!.questionBanks) bankTypeList.add(i.title)
            }
            questionBankType.adapter =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, bankTypeList)

            //btn_scan_submit
            val btnAddAnswer : TextView = holder.itemView.findViewById(R.id.btn_add_answer)
            btnAddAnswer.setOnClickListener {

                val answerDialog = Dialog(context)
                answerDialog.setContentView(R.layout.dialog_create_answer)
                answerDialog.setTitle("新增答案")

                val answerOption: EditText = answerDialog.findViewById(R.id.iv_answer_option_text)!! //答案選項
                val answerDescription: EditText = answerDialog.findViewById(R.id.iv_answer_description_text)!! //答案描述

                val answerChoosePhoto: TextView = answerDialog.findViewById(R.id.answer_choose_photo)!! //選擇圖片 iv_answer_image用於顯示圖片
                answerChoosePhoto.setOnClickListener(View.OnClickListener {
                    //TODO
                    //iv_answer_image
                    var selectBitmap : Bitmap?= null
                    ConstantsDialogFunction.dialogChoosePhotoFromGallery(activity) {
                        bitmap ->
                            if(bitmap!=null){
                                selectBitmap = bitmap
                                selectBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
                                Toast.makeText(context," success choose photo",Toast.LENGTH_SHORT).show()
                                val showImage : ImageView = answerDialog.findViewById(R.id.iv_answer_image)
                                showImage.setImageBitmap(selectBitmap)
                                val selectPhotoBase64String : String = ConstantsFunction.encodeImage(selectBitmap!!)!!
                                ConstantsOcrResults.questionList[position].image?.add(selectPhotoBase64String)
                                imageList.add(selectPhotoBase64String)

                            }else{
                                Toast.makeText(context," choosePhoto has error",Toast.LENGTH_SHORT).show()
                            }
                    }

                    Log.e("in answer pic",selectBitmap.toString())

                })

                val answerCancel : TextView = answerDialog.findViewById(R.id.answer_cancel)
                answerCancel.setOnClickListener(View.OnClickListener {
                    val builder =AlertDialog.Builder(context)
                        .setMessage(" 您確定要取消所有目前的新增結果嗎 ")
                        .setTitle("取消新增答案")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.setPositiveButton("確認") { dialog, which ->
                        btnAddAnswer.text = "新增答案   ⬆"
                        answerDialog.dismiss()
                    }
                    builder.setNegativeButton("取消") { dialog, which ->
                    }
                    builder.show()
                })

                val answerEnter: TextView = answerDialog.findViewById(R.id.answer_enter)!!
                answerEnter.setOnClickListener(View.OnClickListener {
                    //TODO 將結果記錄至常數字串
                    btnAddAnswer.text = "查看答案   ✅"
                    if(answerDescription.text.toString()!=""){
                        ConstantsOcrResults.questionList[position].answerDescription = answerDescription.text.toString()
                    }
                    if(answerOption.text.toString().isNotEmpty()){
                        val str =  answerOption.text.toString()
                        val answerOptionsList : ArrayList<String> = ArrayList(str.split(" "))
                        ConstantsOcrResults.questionList[position].answerOptions = answerOptionsList
                    }else{
                        ConstantsOcrResults.questionList[position].answerOptions?.add("目前為空")
                    }
                    answerDialog.dismiss()
                })
                answerDialog.show()
            }

            val btnScanPhoto : TextView  = holder.itemView.findViewById(R.id.btn_scan_photo)
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
                    val builder =AlertDialog.Builder(context)
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

                    btnScanPhoto.text = "已新增圖片   ✅"
                    //TODO 將結果紀錄至常數字串的position
                    imageDialog.dismiss()
                }
                imageDialog.show()
            }

            val addOptionsButton : ImageButton = holder.itemView.findViewById(R.id.add_options_button)
            addOptionsButton.setOnClickListener{
                if(optionsNum == 10){
                    Toast.makeText(context,"已達最多的選項限制了喔",Toast.LENGTH_SHORT).show()
                }else{
                    optionsNum += 1
                }
                when (optionsNum) {
                    5 -> {
                        option5.visibility = View.VISIBLE
                        Log.e("options5","success")
                    }
                    6 -> {
                        option6.visibility = View.VISIBLE
                    }
                    7-> {
                        option7.visibility = View.VISIBLE
                    }
                    8-> {
                        option8.visibility = View.VISIBLE
                    }
                    9-> {
                        option9.visibility = View.VISIBLE
                    }
                    10-> {
                        option10.visibility = View.VISIBLE
                    }
                }

            }

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
                ConstantsOcrResults.questionList[position].options = options
                ConstantsOcrResults.questionList[position].questionType =ConstantsOcrResults.questionTypeList[questionTypeIndex]

                Log.e("post question",ConstantsOcrResults.questionList[position].toString())

                if(ConstantsOcrResults.questionList[position].answerDescription==""){
                    ConstantsOcrResults.questionList[position].answerDescription = "目前沒有答案描述"
                }

                if(ConstantsOcrResults.questionList[position].title==""){
                    val builder =AlertDialog.Builder(context)
                        .setMessage(" 您的標題(title)不能為空喔 ")
                        .setTitle("確認標題")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()

                }else if(ConstantsOcrResults.questionList[position].number==""){
                    val builder =AlertDialog.Builder(context)
                        .setMessage(" 您的題號(number)不能為空喔 ")
                        .setTitle("確認題號")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                }else if((questionTypeIndex==1||questionTypeIndex==3||questionTypeIndex==4)&&ConstantsOcrResults.questionList[position].options?.size==0){
                    val builder =AlertDialog.Builder(context)
                        .setMessage(" 您的題目選項(options)不能為空喔 ")
                        .setTitle("題目選項")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                    if(ConstantsOcrResults.questionList[position].answerOptions?.size == 0){
                        ConstantsOcrResults.questionList[position].answerOptions?.add("目前答案選項為空")
                    }
                }else if(ConstantsOcrResults.questionList[position].questionBank=="") {
                    val builder = AlertDialog.Builder(context)
                        .setMessage(" 選擇放入的題庫不能為空如果目前無可選擇的請先新增題庫喔 ")
                        .setTitle("選擇放入題庫")
                        .setIcon(R.drawable.baseline_warning_amber_24)
                    builder.show()
                }else{
                    ConstantsQuestionFunction.postQuestionPosition=position
                    ConstantsQuestionFunction.postQuestion( ConstantsOcrResults.questionList[position],activity,
                        onSuccess = {
                            val intent = Intent(activity,ScannerTextWorkSpaceActivity::class.java)
                            activity.startActivity(intent)
                        },
                        onFailure = {
                            it -> Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
                        }
                        )


                }

            }
            val btnScanCancel : TextView  = holder.itemView.findViewById(R.id.btn_scan_cancel)
            btnScanCancel.setOnClickListener {
                //TODO 取消新增
                val builder =AlertDialog.Builder(context)
                    .setMessage(" 您確定要取消這次的掃描結果嗎 ")
                    .setTitle("取消掃描結果")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    ConstantsOcrResults.questionList.removeAt(position)

                    val intent = Intent(context, ScannerTextWorkSpaceActivity::class.java)
                    context.startActivity(intent)
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