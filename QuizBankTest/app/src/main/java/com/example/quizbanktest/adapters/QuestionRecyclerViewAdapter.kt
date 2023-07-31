package com.example.quizbanktest.adapters

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
                                  var questionModel: ArrayList<QuestionModel>, recyclerViewInterface: RecyclerViewInterface
):
    RecyclerView.Adapter<QuestionRecyclerViewAdapter.MyViewHolder>() {

    var recyclerViewInterface = recyclerViewInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        This is where you inflate the layout (Giving a look to our rows)
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_questioncard, parent, false)
            return MyViewHolder(view, recyclerViewInterface)
        } catch (e: Exception) {
            Log.e("BankRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Assigning values to the views we created in the recycler_view_row layout file
//        Based on the position of the recycler view
//        TODO: bankID
        holder.tv_QuestionTitle.text = questionModel[position].title
        holder.tv_QuestionType.text = questionModel[position].questionType
        holder.tv_QuestionCreatedDate.text = questionModel[position].createdDate
//        holder.tv_BankOriginateFrom.text = questionBankModels[position].originateFrom
        holder.tv_QuestionOriginateFrom.append("none")
//        holder.tv_BankCreator.text = questionBankModels[position].creator
        holder.tv_QuestionCreator.append("none")
    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return questionModel.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        //        Grabbing the views from our recycler_view_row layout file
        //        Kinda like in the onCreate method
//        TODO: BankID
        var tv_QuestionTitle: TextView
        var tv_QuestionType: TextView
        var tv_QuestionCreatedDate: TextView
        var tv_QuestionOriginateFrom: TextView
        var tv_QuestionCreator: TextView

        init {

//          TODO: bankID
            tv_QuestionTitle = itemView.findViewById(R.id.question_title)
            tv_QuestionType = itemView.findViewById(R.id.question_type)
            tv_QuestionCreatedDate = itemView.findViewById(R.id.question_createdDate)
            tv_QuestionOriginateFrom = itemView.findViewById(R.id.question_from)
            tv_QuestionCreator = itemView.findViewById(R.id.question_creator)

            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position)
                    }
                }
            })
        }
    }
}