package com.example.quizbanktest.adapters.bank

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction

class QuestionRecyclerViewAdapter(var context: Context,
                                  var activity: AppCompatActivity,
                                  var questionModels: ArrayList<QuestionModel>,
                                  var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<QuestionRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_questioncard, parent, false)
            return MyViewHolder(view, recyclerViewInterface)
        } catch (e: Exception) {
            Log.e("QuestionRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvQuestionTitle.text = questionModels[position].title
        holder.tvQuestionType.text = questionModels[position].questionType
        holder.tvQuestionCreatedDate.text = questionModels[position].createdDate
    }

    override fun getItemCount(): Int {
        return questionModels.size
    }

    fun setItem(position: Int, data: QuestionModel) {
        ConstantsQuestionFunction.putQuestion(activity, data,
            onSuccess = {
                Log.e("QuestionRecyclerViewAdapter", "put successfully")
                questionModels[position] = data
                notifyItemChanged(position)
            },
            onFailure = {
                Log.e("QuestionRecyclerViewAdapter", "put unsuccessfully")
            })
    }

    fun deleteItem(position: Int) {
        ConstantsQuestionFunction.deleteQuestion(activity, questionModels[position]._id.toString(),
            onSuccess = {
                Log.e("QuestionRecyclerViewAdapter", "delete successfully")
                questionModels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, questionModels.size)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "delete unsuccessfully")
            })
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        var tvQuestionTitle: TextView
        var tvQuestionType: TextView
        var tvQuestionCreatedDate: TextView
        var btnEditQuestion: ImageButton

        init {
//          TODO: bankID
            tvQuestionTitle = itemView.findViewById(R.id.question_title)
            tvQuestionType = itemView.findViewById(R.id.question_type)
            tvQuestionCreatedDate = itemView.findViewById(R.id.question_createdDate)
            btnEditQuestion = itemView.findViewById(R.id.btn_edit)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
            btnEditQuestion.setOnClickListener { recyclerViewInterface.settingCard(adapterPosition) }
        }
    }
}