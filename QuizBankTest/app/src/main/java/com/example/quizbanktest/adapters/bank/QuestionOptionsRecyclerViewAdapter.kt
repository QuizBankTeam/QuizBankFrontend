package com.example.quizbanktest.adapters.bank

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

class QuestionOptionsRecyclerViewAdapter(var context: Context,
                                         var questionOptions: ArrayList<String>
) : RecyclerView.Adapter<QuestionOptionsRecyclerViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_bank_question_options_card, parent, false)
            return MyViewHolder(view)
        } catch (e: Exception) {
            Log.e("QuestionOptionsRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvOption.text = questionOptions[position]
    }

    override fun getItemCount(): Int {
        return questionOptions.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvOption: TextView

        init {
            tvOption = itemView.findViewById(R.id.option)
        }
    }

}