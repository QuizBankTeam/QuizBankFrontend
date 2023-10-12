package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.utils.Constants
import okhttp3.internal.userAgent

class QuizMemberInLobbyAdapter(private val context: Activity, private val membersList: ArrayList<QuizMember>):
    RecyclerView.Adapter<QuizMemberInLobbyAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_members_in_lobby, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(membersList[position].userName == Constants.username){
            holder.userName.text = membersList[position].userName + "(你)"
        }else{
            holder.userName.text = membersList[position].userName
        }

    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val userName: TextView = itemView.findViewById(R.id.Quiz_members)
    }
}