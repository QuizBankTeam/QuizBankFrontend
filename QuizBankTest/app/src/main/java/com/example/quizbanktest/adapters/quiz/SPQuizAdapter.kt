package com.example.quizbanktest.adapters.quiz
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.SingleQuiz
import com.example.quizbanktest.models.Quiz

class SPQuizAdapter(private val context: Activity, private val questionList: ArrayList<Quiz>):
    RecyclerView.Adapter<SPQuizAdapter.MyViewHolder>()
{
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_sp_quiz, parent, false)
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
        holder.quizDuringTime.text = "考試時長: " + (currentItem.duringTime?.div(60)).toString() + "分鐘"


        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(context, SingleQuiz::class.java)
            intent.putExtra("Key_id", currentItem._id)
            intent.putExtra("Key_title", currentItem.title)
            intent.putExtra("Key_type", currentItem.type)
            intent.putExtra("Key_status", currentItem.status)
            intent.putExtra("Key_duringTime", currentItem.duringTime)
            intent.putIntegerArrayListExtra("Key_casualDuringTime", currentItem.casualDuringTime)
            intent.putExtra("Key_startDateTime", currentItem.startDateTime)
            intent.putExtra("Key_endDateTime", currentItem.endDateTime)
            intent.putExtra("Key_members", currentItem.members)
            intent.putExtra("quiz_index", position)
            intent.putParcelableArrayListExtra("Key_questions", currentItem.questions)
            context.startActivityForResult(intent, position)
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
        val quizDuringTime: TextView = itemView.findViewById(R.id.Quiz_duringTime)
    }

}