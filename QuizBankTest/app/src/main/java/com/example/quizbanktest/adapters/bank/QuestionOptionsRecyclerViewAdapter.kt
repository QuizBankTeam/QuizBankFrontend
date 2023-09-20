package com.example.quizbanktest.adapters.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.card.MaterialCardView

class QuestionOptionsRecyclerViewAdapter(var context: Context,
                                         var questionType: String,
                                         var questionOptions: ArrayList<String>,
                                         var answerOptions: ArrayList<String>,
                                         var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<QuestionOptionsRecyclerViewAdapter.MyViewHolder>() {

    private lateinit var newOption: String
    private var isModified: Boolean = false
    private var isShowingAnswer: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view: View = when(questionType) {
                "TrueOrFalse" -> {
                    inflater.inflate(R.layout.item_bankquestion_trueorfalse, parent, false)
                }
                else -> {
                    inflater.inflate(R.layout.item_bankquestionoption, parent, false)
                }
            }
            return MyViewHolder(view, recyclerViewInterface, questionOptions, answerOptions)
        } catch (e: Exception) {
            Log.e("QuestionOptionsRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        newOption = questionOptions[position]

        Log.e("QuestionOptionsRecyclerViewAdapter", answerOptions.toString())
        for (item in answerOptions) {
            if (questionOptions[position] == item) {
                if (isShowingAnswer) {
                    Log.e("QuestionOptionsRecyclerViewAdapter", "show answer")
                    holder.tvOption.setBackgroundColor(Color.parseColor("#c6fa73"))
                    holder.tvTitle.setBackgroundColor(Color.parseColor("#c6fa73"))
                    holder.cardView.strokeColor = Color.parseColor("#c6fa73")
                } else {
                    holder.tvOption.setBackgroundColor(Color.parseColor("#ffffff"))
                    holder.tvTitle.setBackgroundColor(Color.parseColor("#ffffff"))
                    holder.cardView.strokeColor = Color.parseColor("#ffffff")
                }
            }
        }

        when(position) {
            0 -> holder.tvTitle.text = "A"
            1 -> holder.tvTitle.text = "B"
            2 -> holder.tvTitle.text = "C"
            3 -> holder.tvTitle.text = "D"
            4 -> holder.tvTitle.text = "E"
            5 -> holder.tvTitle.text = "F"
            6 -> holder.tvTitle.text = "G"
        }

        holder.tvOption.text = questionOptions[position]
        holder.tvOption.movementMethod = ScrollingMovementMethod()
        holder.tvOption.setOnClickListener {
            Log.e("BankQuestionDetailActivity", "show option$position dialog")
            Log.e("BankQuestionDetailActivity", "option$position = " + questionOptions[position])
            val optionDialog = Dialog(context)
            optionDialog.setContentView(R.layout.dialog_bank_question_option)
            optionDialog.show()

            val etOptionDescription =
                optionDialog.findViewById<EditText>(R.id.et_option_description)
            etOptionDescription.setText(questionOptions[position])

            etOptionDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    questionOptions[position] = s.toString()
                    isModified = true
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            optionDialog.setOnDismissListener {
                if (questionOptions[position] != "") {
                    etOptionDescription.setText(questionOptions[position])
                    holder.tvOption.text = questionOptions[position]
                    putQuestion()
                }
            }
        }

    }

    fun showAnswer() {
        isShowingAnswer = !isShowingAnswer
    }

    private fun putQuestion() {

    }

    override fun getItemCount(): Int {
        return questionOptions.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface, questionOptions: ArrayList<String>, answerOptions: ArrayList<String>) : RecyclerView.ViewHolder(itemView) {

        var tvTitle: TextView
        var tvOption: TextView
        lateinit var cardView: MaterialCardView
        var isAnswer: Boolean = false

        init {
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvOption = itemView.findViewById(R.id.tv_option)
            cardView = itemView.findViewById(R.id.cardview_option)

            val position = adapterPosition
//            itemView.setOnClickListener {
//                if (position != RecyclerView.NO_POSITION) {
//                    recyclerViewInterface.onItemClick(position)
//                }
//            }

        }

    }

}