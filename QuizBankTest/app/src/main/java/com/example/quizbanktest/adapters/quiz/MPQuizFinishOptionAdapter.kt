package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.utils.Constants


class MPQuizFinishOptionAdapter(private val context: Activity, private val optionList: ArrayList<Option>, private val answerList: ArrayList<Int>):
    RecyclerView.Adapter<MPQuizFinishOptionAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_option, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.optionContent.text = optionList[position].optionContent
        holder.optionNum.text = optionList[position].optionNum
        if(answerList.contains(position)){
            holder.optionBackground.setBackgroundColor(Color.parseColor("#c6fa73"))
        }
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val optionBackground = itemView.findViewById<LinearLayout>(R.id.option_background)
        val optionNum = itemView.findViewById<TextView>(R.id.optionNum)
        val optionContent = itemView.findViewById<TextView>(R.id.optionContent)
    }
}