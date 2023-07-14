package com.example.yiquizapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yiquizapp.models.BankModel
import com.example.yiquizapp.R
import com.example.yiquizapp.interfaces.RecyclerViewInterface
import com.example.yiquizapp.adapter.BankRecyclerViewAdapter.MyViewHolder

class BankRecyclerViewAdapter(var context: Context, var bankModels: ArrayList<BankModel>, recyclerViewInterface: RecyclerViewInterface) :
    RecyclerView.Adapter<MyViewHolder>() {

    var recyclerViewInterface = recyclerViewInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        This is where you inflate the layout (Giving a look to our rows)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recycler_view_row, parent, false)
        return MyViewHolder(view, recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        Assigning values to the views we created in the recycler_view_row layout file
//        Based on the position of the recycler view
        holder.tv_BankName.text = bankModels[position].bankName
        holder.tv_BankDescription.text = bankModels[position].bankDescription
        holder.tv_BankDate.text = bankModels[position].bankDate
        //        holder.tv_BankImage.setImageResource(bankModels.get(position).getImage());
    }

    override fun getItemCount(): Int {
//        The recycler view just wants to know the number of items you want displayed
        return bankModels.size
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        //        Grabbing the views from our recycler_view_row layout file
        //        Kinda like in the onCreate method
        var tv_BankImage: ImageView? = null
        var tv_BankName: TextView
        var tv_BankDescription: TextView
        var tv_BankDate: TextView

        init {

//            tv_BankImage = itemView.findViewById(R.id.bank_image);
            tv_BankName = itemView.findViewById(R.id.bank_name)
            tv_BankDescription = itemView.findViewById(R.id.bank_description)
            tv_BankDate = itemView.findViewById(R.id.bank_date)

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