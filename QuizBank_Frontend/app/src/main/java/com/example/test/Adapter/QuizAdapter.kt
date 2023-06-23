package com.example.test.Adapter
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.test.Activity.MultiplayerQuiz
import com.example.test.Activity.SingleQuiz
import com.example.test.R
import com.example.test.model.Quiz



class QuizAdapter(private val context: Activity, private val questionList: ArrayList<Quiz>):
    RecyclerView.Adapter<QuizAdapter.MyViewHolder>()
{
    private var onClickListener: OnClickListener? = null
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

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(context, SingleQuiz::class.java)
            intent.putExtra("Key_id", currentItem.id)
            intent.putExtra("Key_title", currentItem.title)
            intent.putExtra("Key_type", currentItem.type)
            intent.putExtra("Key_status", currentItem.status)
            intent.putExtra("Key_duringTime", currentItem.duringTime)
            intent.putExtra("Key_startDate", currentItem.startDate)
            intent.putExtra("Key_endDate", currentItem.endDate)
            intent.putExtra("Key_members", currentItem.members)
            intent.putExtra("Key_questions", currentItem.questions)
            context.startActivity(intent)
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
        // 點擊項目時
//        itemView.setOnClickListener(View.OnClickListener { view ->
//        Toast.makeText(
//            view.context,
//            "click " + getAdapterPosition(), Toast.LENGTH_SHORT
//        ).show()
//        })
    }

}