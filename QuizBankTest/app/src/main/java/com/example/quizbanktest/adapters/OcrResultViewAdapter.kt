package com.example.quizbanktest.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.activity.TagActivity
import com.example.quizbanktest.models.OcrResultModel
import com.example.quizbanktest.utils.ConstantsServiceFunction


class OcrResultViewAdapter(
    private val context: Context,
    private var list: ArrayList<OcrResultModel>
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var optionsNum : Int = 4
        val model = list[position]
        if (holder is MyViewHolder) {

            holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForBank).tags = model.bankList
            holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForQuestion).tags = model.questionList
            holder.itemView.findViewById<co.lujun.androidtagview.TagContainerLayout>(R.id.scannerTagForRange).tags = model.rangList

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            var scannerText : EditText = holder.itemView.findViewById(R.id.iv_scanner_text)
            scannerText.setText(ConstantsOcrResults.getQuestions()[position].description,TextView.BufferType.EDITABLE)
            var chooseTagButton = holder.itemView.findViewById<LinearLayout>(R.id.chooseTag)
            chooseTagButton.setOnClickListener{
                val intent = Intent(context, TagActivity::class.java)
                context.startActivity(intent)
            }
            var questionBankType : Spinner = holder.itemView.findViewById(R.id.spinner_question_bank)
            var bankTypeList : ArrayList<String> = ArrayList()
            var hintType : String = "請選擇將題目新增至下列題庫"
            bankTypeList.add(hintType)

            if(ConstantsServiceFunction.allBanksReturnResponse!=null){
                for(i in ConstantsServiceFunction.allBanksReturnResponse!!.questionBanks) bankTypeList.add(i.title)
            }


            questionBankType.adapter =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, bankTypeList)

            //btn_scan_submit
            var btnAddAnswer : TextView = holder.itemView.findViewById(R.id.btn_add_answer)
            btnAddAnswer.setOnClickListener {
                Toast.makeText(context,"the add button press",Toast.LENGTH_SHORT).show()
                val answerDialog = Dialog(context)
                answerDialog.setContentView(R.layout.dialog_create_answer)
                answerDialog.setTitle("新增答案")

                val answerOption: EditText = answerDialog.findViewById(R.id.iv_answer_option_text)!! //題目描述
                val answerDescription: EditText = answerDialog.findViewById(R.id.iv_answer_description_text)!! //題目描述

                val answerChoosePhoto: TextView = answerDialog.findViewById(R.id.answer_choose_photo)!! //選擇圖片 iv_answer_image用於顯示圖片
                answerChoosePhoto.setOnClickListener(View.OnClickListener {
                    //TODO
                    Toast.makeText(context," answerChoosePhoto",Toast.LENGTH_SHORT).show()
                })
                val answerCancel : TextView = answerDialog.findViewById(R.id.answer_cancel)
                answerCancel.setOnClickListener(View.OnClickListener {
                    var builder =AlertDialog.Builder(context)
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
                    Toast.makeText(context," answerEnter"+answerDescription.text,Toast.LENGTH_SHORT).show()
                    btnAddAnswer.text = "查看答案   ✅"
                    ConstantsOcrResults.questionList[position].answerDescription = answerDescription.text.toString()
                    answerDialog.dismiss()

                })
                answerDialog.show()
            }

            var btnScanPhoto : TextView  = holder.itemView.findViewById(R.id.btn_scan_photo)
            btnScanPhoto.setOnClickListener {
                Toast.makeText(context,"the add image button press",Toast.LENGTH_SHORT).show()
                val imageDialog = Dialog(context)
                imageDialog.setContentView(R.layout.dialog_create_image)
                imageDialog.setTitle("新增圖片")

                val createPhoto: TextView = imageDialog.findViewById(R.id.answer_choose_image)!! //新增圖片(最多三張) iv_answer_image0 iv_answer_image1 iv_answer_image2
                createPhoto.setOnClickListener(View.OnClickListener {
                    //TODO
                    Toast.makeText(context," answerChoosePhoto",Toast.LENGTH_SHORT).show()
                })
                val createImageCancel : TextView = imageDialog.findViewById(R.id.answer_image_cancel)
                createImageCancel.setOnClickListener(View.OnClickListener {
                    var builder =AlertDialog.Builder(context)
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
                    Toast.makeText(context," create image button  Enter",Toast.LENGTH_SHORT).show()
                    btnScanPhoto.text = "已新增圖片   ✅"
                    //TODO 將結果紀錄至常數字串的position
                    imageDialog.dismiss()
                }
                imageDialog.show()
            }

            var btnScanSubmit : TextView  = holder.itemView.findViewById(R.id.btn_scan_submit)
            btnScanSubmit.setOnClickListener {
                //TODO 寫進資料庫
                Toast.makeText(context,"the scan submit button press",Toast.LENGTH_SHORT).show()
            }
            var btnScanCancel : TextView  = holder.itemView.findViewById(R.id.btn_scan_cancel)
            btnScanCancel.setOnClickListener {
                //TODO 取消新增
                Toast.makeText(context,"the scan cancel button press",Toast.LENGTH_SHORT).show()
                var builder =AlertDialog.Builder(context)
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


            var addOptionsButton : ImageButton = holder.itemView.findViewById(R.id.add_options_button)
            addOptionsButton.setOnClickListener{
                if(optionsNum == 10){
                    Toast.makeText(context,"已達最多的選項限制了喔",Toast.LENGTH_SHORT).show()
                }else{
                    optionsNum += 1
                }
                when (optionsNum) {
                    5 -> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option5)
                        optionsEdit.visibility = View.VISIBLE
                        Log.e("options5","success")
                    }
                    6 -> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option6)
                        optionsEdit.visibility = View.VISIBLE
                    }
                    7-> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option7)
                        optionsEdit.visibility = View.VISIBLE
                    }
                    8-> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option8)
                        optionsEdit.visibility = View.VISIBLE
                    }
                    9-> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option9)
                        optionsEdit.visibility = View.VISIBLE
                    }
                    10-> {
                        val optionsEdit : EditText = holder.itemView.findViewById(R.id.question_option10)
                        optionsEdit.visibility = View.VISIBLE
                    }
                }

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
        fun onClick(position: Int, model: OcrResultModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}