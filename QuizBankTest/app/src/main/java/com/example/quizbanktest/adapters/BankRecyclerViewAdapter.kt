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
import com.example.quizbanktest.models.QuestionBankModel

class BankRecyclerViewAdapter(var context: Context,
                              var questionBankModels: ArrayList<QuestionBankModel>, recyclerViewInterface: RecyclerViewInterface
) :
    RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder>() {

    var recyclerViewInterface = recyclerViewInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        This is where you inflate the layout (Giving a look to our rows)
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_bankcard, parent, false)
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
        holder.tv_BankTitle.text = questionBankModels[position].title
        holder.tv_BankType.text = questionBankModels[position].questionBankType
        holder.tv_BankCreatedDate.text = questionBankModels[position].createdDate
//        holder.tv_BankMembers.text = questionBankModels[position].members.joinToString(separator = ",")
        holder.tv_BankMembers.append("3")
//        holder.tv_BankOriginateFrom.text = questionBankModels[position].originateFrom
        holder.tv_BankOriginateFrom.append("none")
//        holder.tv_BankCreator.text = questionBankModels[position].creator
        holder.tv_BankCreator.append("none")
    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return questionBankModels.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        //        Grabbing the views from our recycler_view_row layout file
        //        Kinda like in the onCreate method
//        TODO: BankID
        var tv_BankTitle: TextView
        var tv_BankType: TextView
        var tv_BankCreatedDate: TextView
        var tv_BankMembers: TextView
        var tv_BankOriginateFrom: TextView
        var tv_BankCreator: TextView

        init {

//          TODO: bankID
            tv_BankTitle = itemView.findViewById(R.id.bank_title)
            tv_BankType = itemView.findViewById(R.id.bank_type)
            tv_BankCreatedDate = itemView.findViewById(R.id.bank_createdDate)
            tv_BankMembers = itemView.findViewById(R.id.bank_members)
            tv_BankOriginateFrom = itemView.findViewById(R.id.bank_from)
            tv_BankCreator = itemView.findViewById(R.id.bank_creator)

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