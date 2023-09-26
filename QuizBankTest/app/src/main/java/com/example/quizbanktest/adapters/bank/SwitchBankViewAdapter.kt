package com.example.quizbanktest.adapters.bank

import android.content.Context
import android.graphics.Color
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
import com.example.quizbanktest.models.QuestionBankModel
import com.google.android.material.card.MaterialCardView

class SwitchBankViewAdapter(
    var context: Context,
    var activity: AppCompatActivity,
    var questionBanks: ArrayList<QuestionBankModel>,
    var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BankRecyclerViewAdapter.MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_switchposition_card, parent, false)
            return BankRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface)
        } catch (e: Exception) {
            Log.e("BankRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: BankRecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.tvBankTitle.text = questionBanks[position].title

    }

    override fun getItemCount(): Int {
        return questionBanks.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) :
        RecyclerView.ViewHolder(itemView) {
        private var tvBankTitle: TextView
        private var cardView: MaterialCardView
        private var isClicked: Boolean = false

        init {
            tvBankTitle = itemView.findViewById(R.id.tv_bankTitle)
            cardView = itemView.findViewById(R.id.cardView)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (!isClicked) {
                        cardView.strokeColor = Color.parseColor("#c6fa73")
                        recyclerViewInterface.switchBank(position)
                    } else {
                        cardView.strokeColor = Color.parseColor("#ffffff")
                    }
                }
            }
        }
    }

}