package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Quiz
import java.time.LocalDateTime

class MPQuizFinishSendQAdapter(private val context: Activity, private val quizList: ArrayList<Quiz>):
    RecyclerView.Adapter<MPQuizFinishSendQAdapter.MyViewHolder>()  {

    private var onSendListener: OnSendListener? = null
    interface OnSendListener {
        fun onSend(position: Int)
    }
    fun setOnSendListener(onSendListener: OnSendListener) {
        this.onSendListener = onSendListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_sp_quiz, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = quizList[position]
        val datetime = LocalDateTime.now()

        if(currentItem.startDateTime!=null) {
            val yearDiff = datetime.year - currentItem.startDateTime!!.substring(0, 4).toInt()
            val monthDiff = datetime.toString().substring(5, 7).toInt() - currentItem.startDateTime!!.substring(5, 7).toInt()
            val dayDiff = datetime.dayOfMonth - currentItem.startDateTime!!.substring(8, 10).toInt()
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
        }
        holder.questionNum.text = currentItem.questions?.size.toString() + "題"
        holder.quizStatus.text = currentItem.status
        holder.quizTitle.text = currentItem.title
        holder.quizDuringTime.text = "考試時長: " + (currentItem.duringTime?.div(60)).toString() + "分鐘"
        holder.itemView.setOnClickListener {
            if(onSendListener!=null){
                onSendListener!!.onSend(position)
            }
        }
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val questionNum: TextView = itemView.findViewById(R.id.QuestionNum)
        val quizStatus: TextView = itemView.findViewById(R.id.QuizStatus)
        val quizStartDate: TextView = itemView.findViewById(R.id.QuizStartDate)
        val quizTitle: TextView = itemView.findViewById(R.id.QuizTitle)
        val quizDuringTime: TextView = itemView.findViewById(R.id.Quiz_duringTime)
    }
}