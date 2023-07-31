package com.example.quizbanktest.adapters.bank

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

import com.example.quizbanktest.models.QuestionModel

class QuestionRecyclerViewAdapter(var context: Context,
                                  var questionModels: ArrayList<QuestionModel>,
                                  var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<QuestionRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        This is where you inflate the layout (Giving a look to our rows)
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
//        Assigning values to the views we created in the recycler_view_row layout file
//        Based on the position of the recycler view
//        TODO: bankID
        holder.tvQuestionTitle.text = questionModels[position].title
        holder.tvQuestionType.text = questionModels[position].questionType
        holder.tvQuestionCreatedDate.text = questionModels[position].createdDate
    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return questionModels.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        //        Grabbing the views from our recycler_view_row layout file
        //        Kinda like in the onCreate method
//        TODO: BankID
        var tvQuestionTitle: TextView
        var tvQuestionType: TextView
        var tvQuestionCreatedDate: TextView

        init {
//          TODO: bankID
            tvQuestionTitle = itemView.findViewById(R.id.question_title)
            tvQuestionType = itemView.findViewById(R.id.question_type)
            tvQuestionCreatedDate = itemView.findViewById(R.id.question_createdDate)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
        }
    }
}