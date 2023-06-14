package com.example.test
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class QuizAdapter(private val context: Activity, private val questionList: ArrayList<Quiz>):
    RecyclerView.Adapter<QuizAdapter.MyViewHolder>()
{
    private var onClickListener: View.OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.quiz_row, null)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
        holder.quizNum.text = currentItem.questions.size.toString() + "題"
        holder.quizStatus.text = currentItem.status
        holder.quizStartDate.text = currentItem.startDate
        holder.quizTitle.text = currentItem.title
        holder.quizMembers.text = currentItem.members.toString()
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val quizNum: TextView = itemView.findViewById(R.id.QuizNum)
        val quizStatus: TextView = itemView.findViewById(R.id.QuizStatus)
        val quizStartDate: TextView = itemView.findViewById(R.id.QuizStartDate)
        val quizTitle: TextView = itemView.findViewById(R.id.QuizTitle)
        val quizMembers: TextView = itemView.findViewById(R.id.QuizMembers)
        // 點擊項目時
//        itemView.setOnClickListener(View.OnClickListener { view ->
//        Toast.makeText(
//            view.context,
//            "click " + getAdapterPosition(), Toast.LENGTH_SHORT
//        ).show()
//        })
    }

}