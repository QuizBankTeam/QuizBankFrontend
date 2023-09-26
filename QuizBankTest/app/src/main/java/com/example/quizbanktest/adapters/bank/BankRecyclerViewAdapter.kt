package com.example.quizbanktest.adapters.bank

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction

class BankRecyclerViewAdapter(var context: Context,
                              var activity: AppCompatActivity,
                              var questionBankModels: ArrayList<QuestionBankModel>,
                              var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
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

        holder.tvBankTitle.text = questionBankModels[position].title
        holder.tvBankType.text = questionBankModels[position].questionBankType
        holder.tvBankCreatedDate.text = questionBankModels[position].createdDate

    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return questionBankModels.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(data: QuestionBankModel) {
        questionBankModels.add(data)
        notifyDataSetChanged()
    }

    fun setItem(position: Int, data: QuestionBankModel) {
        ConstantsQuestionBankFunction.putQuestionBank(activity, data,
            onSuccess = {
                Log.e("BankRecyclerViewAdapter", "put successfully")
                questionBankModels[position] = data
                notifyItemChanged(position)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "put unsuccessfully")
            })
    }

    fun deleteItem(position: Int) {
        ConstantsQuestionBankFunction.deleteQuestionBank(activity, questionBankModels[position]._id,
            onSuccess = {
                Log.e("BankRecyclerViewAdapter", "delete successfully")
                questionBankModels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, questionBankModels.size)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "delete unsuccessfully")
            })
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        var tvBankTitle: TextView
        var tvBankType: TextView
        var tvBankCreatedDate: TextView
        var btnEditBank: ImageButton

        init {
            tvBankTitle = itemView.findViewById(R.id.bank_title)
            tvBankType = itemView.findViewById(R.id.bank_type)
            tvBankCreatedDate = itemView.findViewById(R.id.bank_createdDate)
            btnEditBank = itemView.findViewById(R.id.btn_edit)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
            btnEditBank.setOnClickListener { recyclerViewInterface.settingCard(adapterPosition) }
        }
    }
}