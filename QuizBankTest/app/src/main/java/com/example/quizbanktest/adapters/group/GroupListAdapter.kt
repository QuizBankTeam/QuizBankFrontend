package com.example.quizbanktest.adapters.group

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.group.GroupListActivity
import com.example.quizbanktest.activity.group.GroupPageActivity
import com.example.quizbanktest.models.GroupModel
import com.example.quizbanktest.models.QuestionBankModel

open class GroupListAdapter(
    private val context: Context,
    private var list: ArrayList<GroupModel>,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_group_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            val memberText = "群組成員 ("+model.members?.size.toString()+") "+": "+model.members?.joinToString(" ")
            holder.itemView.findViewById<TextView>(R.id.group_title).text = model.name
            holder.itemView.findViewById<TextView>(R.id.group_members).text = memberText


            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
                val intent = Intent(context,GroupPageActivity::class.java)
                intent.putExtra("group_name",model.name)
                intent.putExtra("members:",model.members)
                context.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }



    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: GroupModel)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}