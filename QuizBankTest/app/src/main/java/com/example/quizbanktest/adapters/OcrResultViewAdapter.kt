package com.example.quizbanktest.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.ScannerTextWorkSpaceActivity
import com.example.quizbanktest.activity.TagActivity

import com.example.quizbanktest.models.OcrResultModel


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