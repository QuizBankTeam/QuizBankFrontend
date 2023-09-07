package com.example.quizbanktest.adapters.quiz
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.utils.Constants

class QuestionAddQuizAdapter(private val context: Activity, private val quizList: ArrayList<Quiz>, private val quizType: String):
    RecyclerView.Adapter<QuestionAddQuizAdapter.MyViewHolder>()
{
    private var onClickListener: SelectOnClickListener? = null
    fun setOnClickListener(onClickListener: SelectOnClickListener) {
        this.onClickListener = onClickListener
    }
    interface SelectOnClickListener {
        fun onclick(position: Int, holder: MyViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_mp_quiz, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = quizList[position]
        val datetime = LocalDateTime.now()
        if(quizType==Constants.quizTypeSingle){
            currentItem.members?.let {
                holder.quizMembers.text = ""
            }
        }else if(quizType==Constants.quizTypeCasual){
            currentItem.members?.let {
                holder.quizMembers.text = context.getString(R.string.members_CN) + ": ${it.size}人"
            }
        }
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
            if (this.onClickListener != null) {
                onClickListener!!.onclick(position, holder)
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
        val quizMembers: TextView = itemView.findViewById(R.id.QuizMembers)
    }

}