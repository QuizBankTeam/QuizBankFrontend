package com.example.quizbanktest.adapters.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.bank.BankQuestionActivity
import com.example.quizbanktest.activity.quiz.SPSingleRecord
import com.example.quizbanktest.models.QuestionBankModel

open class RecentViewAdapter(
    private val context: Context,
    private var list: ArrayList<QuestionBankModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_quizbank,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.recentTitle).text = model.title
            holder.itemView.findViewById<TextView>(R.id.recentType).text = model.questionBankType
            holder.itemView.findViewById<TextView>(R.id.recentDate).text = model.createdDate

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
                val bankQuestionActivity = Intent()
                bankQuestionActivity.setClass(context, BankQuestionActivity::class.java)

                bankQuestionActivity.putExtra("BankTitle", model.title)
                bankQuestionActivity.putExtra("BankId", model._id)
                Log.e("BankActivity", "start bankQuestion activity11")

                context.startActivity(bankQuestionActivity)
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
        fun onClick(position: Int, model: QuestionBankModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}