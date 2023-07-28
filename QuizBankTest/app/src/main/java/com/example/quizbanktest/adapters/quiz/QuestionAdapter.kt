package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.SingleQuestion
import com.example.quizbanktest.models.Question

class QuestionAdapter(private val context: Activity, private val questionList: ArrayList<Question>, private val casualDuringTime: ArrayList<Int>):
    RecyclerView.Adapter<QuestionAdapter.MyViewHolder>()
{
    private lateinit var quizType: String
    private var quizIndex: Int = 0
    fun setQuizType(type: String){
        quizType=type
    }
    fun setQuizIndex(index: Int){
        quizIndex = index
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_question, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
        holder.questionBank.text = "題庫: " + currentItem.questionBank
        holder.questionProvider.text = "提供者: " + currentItem.provider
        holder.questionTitle.text = currentItem.title
        holder.questionType.text = if(currentItem.questionType=="MultipleChoiceS") "單選"
                                else if(currentItem.questionType=="MultipleChoiceM") "多選"
                                else if(currentItem.questionType=="TrueOrFalse") "是非"
                                else if(currentItem.questionType=="ShortAnswer") "簡答"
                                else "填充"
        if (currentItem.image != null) {
            if(currentItem.image!!.isNotEmpty()){
                val imageBytes: ByteArray = Base64.decode(currentItem.image!![0], Base64.DEFAULT)
                val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.questionImage.setImageBitmap(decodeImage)
            }
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
            intent.putExtra("Key_id", currentItem._id)
            intent.putExtra("Key_title", currentItem.title)
            intent.putExtra("Key_type", currentItem.questionType)
            intent.putStringArrayListExtra("Key_tag", currentItem.tag)
            intent.putExtra("Key_description", currentItem.description)
            intent.putStringArrayListExtra("Key_image", currentItem.image)
            intent.putExtra("Key_answerDescription", currentItem.answerDescription)
            intent.putExtra("Key_number", currentItem.number)
            intent.putExtra("Key_questionBank", currentItem.questionBank)
            intent.putExtra("Key_provider", currentItem.provider)
            intent.putExtra("Key_createdDate", currentItem.createdDate)
            intent.putExtra("Key_quizType", quizType)
            intent.putStringArrayListExtra("Key_answerOptions", currentItem.answerOptions)
            intent.putStringArrayListExtra("Key_options", currentItem.options)
            intent.putExtra("question_index", position)
            intent.putExtra("quiz_index", quizIndex)

            if(quizType=="casual"){
                intent.putExtra("Key_timeLimit", casualDuringTime[position])
            }
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