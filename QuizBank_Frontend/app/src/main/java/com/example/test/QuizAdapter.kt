package com.example.test
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuizAdapter(private val context: Activity, private val questionList: ArrayList<Quiz>):
    RecyclerView.Adapter<QuestionAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.option_row, null)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = optionsList[position]
        holder.optionContent.text = currentItem.optionContent
        holder.optionNum.text = currentItem.optionNum
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val optionNum: TextView = itemView.findViewById(R.id.optionNum)
        val optionContent: TextView = itemView.findViewById(R.id.optionContent)
    }
}