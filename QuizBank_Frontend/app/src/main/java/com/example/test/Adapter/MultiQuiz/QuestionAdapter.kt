package com.example.test.Adapter.MultiQuiz

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.Activity.MultiQuiz.SingleQuestion
import com.example.test.R
import com.example.test.model.Question


class QuestionAdapter(private val context: Activity, private val questionList: ArrayList<Question>, private val casualDuringTime: ArrayList<Int>):
    RecyclerView.Adapter<QuestionAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.mp_question_row, null)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
        holder.questionBank.text = "題庫: " + currentItem.questionBank
        holder.questionImage.setImageResource(currentItem.image)
        holder.questionProvider.text = "提供者: " + currentItem.provider
        holder.questionTitle.text = currentItem.title

        if(currentItem.type == "MultipleChoiceS") {
            holder.questionType.text = "單選"
        }
        else if(currentItem.type == "MultipleChoiceM") {
            holder.questionType.text = "多選"
        }
        if(currentItem.tag!=null)
        {
            if(currentItem.tag!!.size==0) {
                holder.questionContainer.removeAllViews()
            }
            else if(currentItem.tag!!.size==1) {
                holder.questionTag0.text = currentItem.tag!![0]
                holder.questionContainer.removeView(holder.questionTag1)
                holder.questionContainer.removeView(holder.questionTag2)
            }
            else if(currentItem.tag!!.size==2) {
                holder.questionTag0.text = currentItem.tag!![0]
                holder.questionTag1.text = currentItem.tag!![1]
                holder.questionContainer.removeView(holder.questionTag2)
            }
            else if(currentItem.tag!!.size==3) {
                holder.questionTag0.text = currentItem.tag!![0]
                holder.questionTag1.text = currentItem.tag!![1]
                holder.questionTag2.text = currentItem.tag!![2]
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.setClass(context, SingleQuestion::class.java)
            intent.putExtra("Key_id", currentItem.id)
            intent.putExtra("Key_title", currentItem.title)
            intent.putExtra("Key_type", currentItem.type)
            intent.putStringArrayListExtra("Key_tag", currentItem.tag)
            intent.putExtra("Key_timeLimit", casualDuringTime[position])
            intent.putExtra("Key_description", currentItem.description)
            intent.putExtra("Key_image", currentItem.image)
            intent.putExtra("Key_answerDescription", currentItem.answerDescription)
            intent.putExtra("Key_number", currentItem.number)
            intent.putExtra("Key_questionBank", currentItem.questionBank)
            intent.putExtra("Key_provider", currentItem.provider)
            intent.putExtra("Key_createdDate", currentItem.createdDate)
            intent.putStringArrayListExtra("Key_answerOptions", currentItem.answerOption)
            intent.putStringArrayListExtra("Key_options", currentItem.options)

            context.startActivityForResult(intent, position)

        }

    }

    fun updateTimeLimit(timeLimit: Int, position: Int){
        casualDuringTime[position] = timeLimit
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
        val questionContainer: LinearLayout = itemView.findViewById(R.id.question_tag_container)
    }
}