package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.SPSingleRecord
import com.example.quizbanktest.models.QuizRecord
import org.w3c.dom.Text
import java.time.LocalDateTime

class SPRecordAdapter(private val context: Activity, private val recordList: ArrayList<QuizRecord>):
    RecyclerView.Adapter<SPRecordAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_sp_record, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recordList[position]
        val activityRecordPage = "RecordPage"
        val datetime = LocalDateTime.now()
        val yearDiff = datetime.year - currentItem.startDateTime.substring(0, 4).toInt()
        val monthDiff = datetime.toString().substring(5, 7).toInt() - currentItem.startDateTime.substring(5, 7).toInt()
        val dayDiff = datetime.dayOfMonth - currentItem.startDateTime.substring(8, 10).toInt()
        if(yearDiff!=0){
            if(yearDiff>0)
                holder.quizStartDate.text = yearDiff.toString() + "年前"
            else
                holder.quizStartDate.text = yearDiff.toString() + "年後"
        }else if(monthDiff!=0){
            if(monthDiff>0)
                holder.quizStartDate.text = monthDiff.toString() + "個月前"
            else
                holder.quizStartDate.text = monthDiff.toString() + "個月後"
        }else if(dayDiff!=0){
            if(dayDiff>0)
                holder.quizStartDate.text = dayDiff.toString() + "天前"
            else
                holder.quizStartDate.text = dayDiff.toString() + "天後"
        }else{
            holder.quizStartDate.text = "今天"
        }

        holder.quizDuringTime.text = "考試時長: " + (currentItem.duringTime?.div(60)).toString() + "分鐘"
        holder.quizRecordTitle.text = currentItem.title
        holder.questionNum.text = currentItem.questionRecords.size.toString() + "題"
        holder.score.text = "得分: " + currentItem.totalScore.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(context, SPSingleRecord::class.java)
            intent.putExtra("previousActivity", activityRecordPage)
            intent.putExtra("Key_quizRecord", recordList[position])
            context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val questionNum: TextView = itemView.findViewById(R.id.QuestionNum)
        val quizRecordTitle: TextView = itemView.findViewById(R.id.QuizRecordTitle)
        val quizDuringTime: TextView = itemView.findViewById(R.id.Quiz_duringTime)
        val score: TextView = itemView.findViewById(R.id.quiz_score)
        val quizStartDate: TextView = itemView.findViewById(R.id.QuizStartDate)
    }
}