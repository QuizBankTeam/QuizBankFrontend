package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.QuestionBankModel

class QuestionAddSBAdapter(private val context: Activity, private val bankList: ArrayList<QuestionBankModel>):
    RecyclerView.Adapter<QuestionAddSBAdapter.MyViewHolder>() {
    private var selectOnClickListener: QuestionAddSBAdapter.SelectOnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.row_question_add_from_single_bank, parent, false)
        return MyViewHolder(itemView)
    }

    interface SelectOnClickListener {
        fun onclick(position: Int, holder: QuestionAddSBAdapter.MyViewHolder)
    }

    fun setSelectClickListener(selectOnClickListener: SelectOnClickListener) {
        this.selectOnClickListener = selectOnClickListener
    }

    override fun getItemCount(): Int {
        return bankList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bankList[position]



        holder.bankTitle.text = currentItem.title
        holder.bankType.text = currentItem.questionBankType
        holder.bankCreatedDate.text = currentItem.createdDate
        holder.bankMembers.text = "成員: " + currentItem.members[0] + "..."
        holder.bankCreator.text = currentItem.creator

        holder.itemView.setOnClickListener {
            if (this.selectOnClickListener != null) {
                selectOnClickListener!!.onclick(position, holder)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bankTitle: TextView = itemView.findViewById(R.id.bank_title)
        var bankType: TextView = itemView.findViewById(R.id.bank_type)
        var bankCreatedDate: TextView = itemView.findViewById(R.id.bank_createdDate)
        var bankMembers: TextView = itemView.findViewById(R.id.bank_members)
        var bankCreator: TextView = itemView.findViewById(R.id.bank_creator)

    }
}