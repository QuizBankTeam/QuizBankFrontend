package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Option

class OptionAdapter(private val context: Activity, private  val arrayList: ArrayList<Option>) :
    RecyclerView.Adapter<OptionAdapter.MyViewHolder>() {

    private var answerOptions: ArrayList<Int> = ArrayList()
    private var inSingleQuestion: Boolean = false
    private var inRecord: Boolean = false
    private var selectOption: ArrayList<Int> = ArrayList()
    private var answerIsCorrect: Boolean = false
    private var selectOnClickListener: SelectOnClickListener? = null

    interface SelectOnClickListener {
        fun onclick(position: Int, holder: MyViewHolder)
    }

    fun setSelectClickListener(selectOnClickListener: SelectOnClickListener) {
        this.selectOnClickListener = selectOnClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_option, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = arrayList[position]
        holder.optionContent.text = currentItem.optionContent
        holder.optionNum.text = currentItem.optionNum
        holder.itemView.setOnClickListener {
            if (this.selectOnClickListener != null) {
                selectOnClickListener!!.onclick(position, holder)
            }
        }
        if (inSingleQuestion) {
            adaptSingleQuestion(holder, position)
        } else if (inRecord) {
            adaptInRecord(holder, position)
        }
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionBackground = itemView.findViewById<LinearLayout>(R.id.option_background)
        val optionNum = itemView.findViewById<TextView>(R.id.optionNum)
        val optionContent = itemView.findViewById<TextView>(R.id.optionContent)
    }

    private fun adaptSingleQuestion(holder: MyViewHolder, position: Int) {
        if(position in answerOptions){
            holder.optionBackground.setBackgroundColor(Color.parseColor("#c6fa73"))
        }else{
            holder.optionBackground.setBackgroundColor(0)
        }
    }

    private fun adaptInRecord(holder: MyViewHolder, position: Int) {
        for (item in answerOptions) {
            if (item == position) {
                holder.optionBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.answer_correct
                    )
                )
            }
        }
        for (item in selectOption) {
            if (item == position) {
                holder.optionContent.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.baseline_edit,
                    0
                )
            }
        }
        if (position == arrayList.size - 1) {
            holder.optionBackground.removeView(holder.optionNum)
//            holder.optionBackground.setPadding(20, 10, 20, 10)
            holder.optionContent.gravity = Gravity.CENTER
            if (answerIsCorrect) {
                holder.optionBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.answer_correct
                    )
                )
            } else {
                holder.optionBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_red2
                    )
                )
            }
        }
    }

    fun setInSingleQuestion(inQuiz: Boolean) {
        inSingleQuestion = inQuiz
    }

    fun setRecord(
        inRecord: Boolean,
        answerIsCorrect: Boolean,
        selectOptions: ArrayList<Int>,
        answerOptions: ArrayList<Int>
    ) {
        this.selectOption = selectOptions
        this.inRecord = inRecord
        this.answerIsCorrect = answerIsCorrect
        this.answerOptions = answerOptions
    }

    fun setAnswerOptions(answerOptions: ArrayList<Int>) {
        this.answerOptions = answerOptions
    }

    fun itemChange(change: ArrayList<Int>){
        for(index in change){
            notifyItemChanged(index)
        }
    }
}
