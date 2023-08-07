package com.example.quizbanktest.adapters.quiz
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.quizbanktest.R
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.activity.quiz.SingleQuiz
import com.example.quizbanktest.models.Quiz
import java.time.LocalDateTime


class MPQuizAdapter(private val context: Activity, private val questionList: ArrayList<Quiz>):
    RecyclerView.Adapter<MPQuizAdapter.MyViewHolder>()
{
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_mp_quiz, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
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
        holder.quizNum.text = currentItem.questions?.size.toString() + "題"
        holder.quizStatus.text = currentItem.status
        holder.quizTitle.text = currentItem.title
        val tmpMember = currentItem.members
        var member = "成員: "
        if(tmpMember!=null){
            if(tmpMember.size  > 0) {
                for(i in tmpMember.indices){
                    if(i>3) {
                        member += "..."
                        break
                    }
                    member += tmpMember[i]
                    member += " "
                }
            }
            else {
                member = "無成員"
            }
        }

        holder.quizMembers.text = member

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(context, SingleQuiz::class.java)
            intent.putExtra("Key_id", currentItem._id)
            intent.putExtra("Key_title", currentItem.title)
            intent.putExtra("Key_type", currentItem.type)
            intent.putExtra("Key_status", currentItem.status)
            intent.putExtra("Key_duringTime", currentItem.duringTime)
            intent.putExtra("Key_startDate", currentItem.startDateTime)
            intent.putExtra("Key_endDate", currentItem.endDateTime)
            intent.putExtra("Key_members", currentItem.members)
            intent.putParcelableArrayListExtra("Key_questions", currentItem.questions)
            intent.putIntegerArrayListExtra("Key_casualDuringTime", currentItem.casualDuringTime)
            context.startActivityForResult(intent, position)
            Log.d("in quizAdapter position=", position.toString())
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Quiz)
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val quizNum: TextView = itemView.findViewById(R.id.QuizNum)
        val quizStatus: TextView = itemView.findViewById(R.id.QuizStatus)
        val quizStartDate: TextView = itemView.findViewById(R.id.QuizStartDate)
        val quizTitle: TextView = itemView.findViewById(R.id.QuizTitle)
        val quizMembers: TextView = itemView.findViewById(R.id.QuizMembers)
    }

}