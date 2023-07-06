package com.example.test.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.model.Quiz
import com.example.test.model.QuizRecord

class SPRecordAdapter(private val context: Activity, private val recordList: ArrayList<QuizRecord>):
    RecyclerView.Adapter<SPQuizAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SPQuizAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.sp_record_row, parent, false)
        return SPQuizAdapter.MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: SPQuizAdapter.MyViewHolder, position: Int) {
        val currentItem = recordList[position]

        holder.quizDuringTime.text = "考試時長: " + (currentItem.duringTime?.div(60)).toString() + "分鐘"
        holder.quizNum.text = "得分: " + currentItem.questionRecords.size.toString() + " / " + "10"
        holder.quizStartDate.text = currentItem.startDate.toString()
        holder.quizTitle.text = currentItem.title
        holder.quizNum.text = currentItem.questionRecords.size.toString() + "題"
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val quizNum: TextView = itemView.findViewById(R.id.QuizNum)
        val quizTitle: TextView = itemView.findViewById(R.id.QuizTitle)
        val quizDuringTime: TextView = itemView.findViewById(R.id.Quiz_duringTime)
        val score: TextView = itemView.findViewById(R.id.quiz_score)

    }
}