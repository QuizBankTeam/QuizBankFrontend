package com.example.test.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.model.Question

class QuestionAdapter(private val context: Activity, private val questionList: ArrayList<Question>):
    RecyclerView.Adapter<QuestionAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.question_row, null)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
        holder.questionBank.text = currentItem.questionBank
        holder.questionImage.setImageResource(currentItem.image)
        holder.questionProvider.text = currentItem.questionBank
        holder.questionTitle.text = currentItem.questionBank
        holder.questionType.text = currentItem.questionBank
        holder.questionTag0.text = currentItem.tag[0]
        holder.questionTag1.text = currentItem.tag[1]
        if (currentItem.tag.size > 2)
        {
            holder.questionTag2.text = currentItem.tag[2]
        }
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val questionBank: TextView = itemView.findViewById(R.id.question_bank)
        val questionImage: ImageView = itemView.findViewById(R.id.question_image)
        val questionProvider: TextView = itemView.findViewById(R.id.question_provider)
        val questionTitle: TextView = itemView.findViewById(R.id.question_title)
        val questionType: TextView = itemView.findViewById(R.id.question_type)
        val questionTag0: TextView = itemView.findViewById(R.id.question_tag0)
        val questionTag1: TextView = itemView.findViewById(R.id.question_tag1)
        val questionTag2: TextView = itemView.findViewById(R.id.question_tag2)
    }
}