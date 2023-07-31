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
import com.example.quizbanktest.models.QuestionBankModel

class BankRecyclerViewAdapter(var context: Context,
                              var questionBankModels: ArrayList<QuestionBankModel>,
                              var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder>() {

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
        holder.tvBankTitle.text = questionBankModels[position].title
        holder.tvBankType.text = questionBankModels[position].questionBankType
        holder.tvBankCreatedDate.text = questionBankModels[position].createdDate
//        holder.tv_BankMembers.text = questionBankModels[position].members.joinToString(separator = ",")
        holder.tvBankMembers.append("3")
        holder.tvBankOriginateFrom.append("none")
        holder.tvBankCreator.append("none")
    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return questionBankModels.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        //        Grabbing the views from our recycler_view_row layout file
        //        Kinda like in the onCreate method
//        TODO: BankID
        var tvBankTitle: TextView
        var tvBankType: TextView
        var tvBankCreatedDate: TextView
        var tvBankMembers: TextView
        var tvBankOriginateFrom: TextView
        var tvBankCreator: TextView

        init {

//          TODO: bankID
            tvBankTitle = itemView.findViewById(R.id.bank_title)
            tvBankType = itemView.findViewById(R.id.bank_type)
            tvBankCreatedDate = itemView.findViewById(R.id.bank_createdDate)
            tvBankMembers = itemView.findViewById(R.id.bank_members)
            tvBankOriginateFrom = itemView.findViewById(R.id.bank_from)
            tvBankCreator = itemView.findViewById(R.id.bank_creator)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
        }
    }
}