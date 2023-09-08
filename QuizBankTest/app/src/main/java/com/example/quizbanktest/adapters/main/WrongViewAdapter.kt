package com.example.quizbanktest.adapters.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.QuestionRecord
import com.example.quizbanktest.models.QuizRecord

class WrongViewAdapter (private val context: Context,
                        private var list: ArrayList<QuizRecord>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_wrong,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            holder.itemView.findViewById<TextView>(R.id.wrongTitle).text = model.title

            if(model.totalScore==-1){
                holder.itemView.findViewById<TextView>(R.id.wrongType).text = "得分: 0"
            }else{
                holder.itemView.findViewById<TextView>(R.id.wrongType).text = "得分: " + model.totalScore.toString()
            }
            holder.itemView.findViewById<TextView>(R.id.wrongDate).text = "考試時長: " + (model.duringTime?.div(60)).toString() + "分鐘"

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
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
        fun onClick(position: Int, model: QuizRecord)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}