package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.QuizRecord

class SPRecordAdapter(private val context: Activity, private val recordList: ArrayList<QuizRecord>):
    RecyclerView.Adapter<SPRecordAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SPRecordAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_sp_record, parent, false)
        return SPRecordAdapter.MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: SPRecordAdapter.MyViewHolder, position: Int) {
        val currentItem = recordList[position]

        holder.quizDuringTime.text = "考試時長: " + (currentItem.duringTime?.div(60)).toString() + "分鐘"
        holder.quizNum.text = "得分: " + currentItem.questionRecords.size.toString() + " / " + "10"
        holder.quizTitle.text = currentItem.title
        holder.quizNum.text = currentItem.questionRecords.size.toString() + "題"
        holder.score.text = currentItem.totalScore.toString()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val quizNum: TextView = itemView.findViewById(R.id.QuizNum)
        val quizTitle: TextView = itemView.findViewById(R.id.QuizTitle)
        val quizDuringTime: TextView = itemView.findViewById(R.id.Quiz_duringTime)
        val score: TextView = itemView.findViewById(R.id.quiz_score)

    }
}