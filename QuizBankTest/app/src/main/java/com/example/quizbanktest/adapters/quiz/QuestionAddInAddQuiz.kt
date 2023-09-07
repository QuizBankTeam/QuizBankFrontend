package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.Constants
import org.w3c.dom.Text

class QuestionAddInAddQuiz(private val context: Activity, private val questionList: ArrayList<Question>):
    RecyclerView.Adapter<QuestionAddInAddQuiz.MyViewHolder>()
{
    private var onDeleteClickListener: QuestionAddInAddQuiz.OnDeleteClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_question_add_in_add_quiz, parent, false)
        return MyViewHolder(itemView)
    }
    interface OnDeleteClickListener {
        fun onclick(position: Int, holder: MyViewHolder)
    }

    fun setOnDeleteClickListener(onClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = questionList[position]
        holder.questionTitle.text = currentItem.title
        holder.questionNumber.text = currentItem.number + "."
        holder.questionType.text = if(currentItem.questionType=="MultipleChoiceS") context.getString(R.string.MultipleChoiceS_CN)
        else if(currentItem.questionType=="MultipleChoiceM") context.getString(R.string.MultipleChoiceM_CN)
        else if(currentItem.questionType=="TrueOrFalse") context.getString(R.string.TrueOrFalse_CN)
        else if(currentItem.questionType=="ShortAnswer") context.getString(R.string.ShortAnswer_CN)
        else context.getString(R.string.Filling_CN)
        holder.questionDescription.text = currentItem.description


        if(currentItem.questionImage!=null){
            for(item in currentItem.questionImage!!){
                val imageBytes: ByteArray = Base64.decode(item, Base64.DEFAULT)
                val decodeImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.questionImage.setImageBitmap(decodeImage)
                break
            }
        }

        if(currentItem.tag!=null) {
            if(currentItem.tag!!.size==0) {
                holder.questionTagIcon.visibility = View.GONE
            }
            else{
                holder.questionTagIcon.visibility = View.VISIBLE
                var allTags= ""
                for(tag in currentItem.tag!!){
                    allTags = "$allTags$tag "
                }
                holder.questionTag.text = allTags
            }
        }

        holder.deleteBtn.setOnClickListener {
            if(this.onDeleteClickListener!=null){
                onDeleteClickListener!!.onclick(position, holder)
            }
        }

    }



    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val questionImage: ImageView = itemView.findViewById(R.id.question_image)
        val questionTitle: TextView = itemView.findViewById(R.id.question_title)
        val questionNumber: TextView = itemView.findViewById(R.id.question_number)
        val questionType: TextView = itemView.findViewById(R.id.question_type)
        val questionTag: TextView = itemView.findViewById(R.id.question_tag)
        val questionDescription: TextView = itemView.findViewById(R.id.question_description)
        val questionTagIcon: TextView = itemView.findViewById(R.id.question_tag_icon)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}