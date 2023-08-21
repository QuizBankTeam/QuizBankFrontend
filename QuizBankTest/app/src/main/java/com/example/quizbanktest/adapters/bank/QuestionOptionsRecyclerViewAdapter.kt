package com.example.quizbanktest.adapters.bank

import android.app.Dialog
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.bank.BankQuestionDetailActivity
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface

class QuestionOptionsRecyclerViewAdapter(var context: Context,
                                         var questionOptions: ArrayList<String>,
) : RecyclerView.Adapter<QuestionOptionsRecyclerViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_bankquestionoption, parent, false)
            return MyViewHolder(view)
        } catch (e: Exception) {
            Log.e("QuestionOptionsRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvOption.text = questionOptions[position]
        holder.tvOption.movementMethod = ScrollingMovementMethod()
        holder.tvOption.setOnClickListener {
            Log.e("BankQuestionDetailActivity", "show option dialog")
            val optionDialog = Dialog(context)
            optionDialog.setContentView(R.layout.dialog_bank_question_option)
            optionDialog.show()

            val etOptionDescription =
                optionDialog.findViewById<EditText>(R.id.et_option_description)
            etOptionDescription.setText(questionOptions[position])
        }
    }

    override fun getItemCount(): Int {
        return questionOptions.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvOption: TextView

        init {
            tvOption = itemView.findViewById(R.id.tv_option)
        }
    }

}