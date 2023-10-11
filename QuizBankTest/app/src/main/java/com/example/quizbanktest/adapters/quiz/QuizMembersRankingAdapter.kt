package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.Constants

class QuizMembersRankingAdapter(private val context: Activity, private val membersList: ArrayList<QuizMember>, private val totalQNum: Int):
    RecyclerView.Adapter<QuizMembersRankingAdapter.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_member_rank, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val correctNum = membersList[position].correctAnswerNum
        val incorrectNum = totalQNum - correctNum
        val correctDiff = correctNum - incorrectNum
        when {
            correctDiff < -2 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image0)
            }
            correctDiff == -2 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image1)
            }
            correctDiff == -1 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image2)
            }
            correctDiff == 0 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image3)
            }
            correctDiff == 1 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image4)
            }
            correctDiff == 2 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image5)
            }
            correctDiff == 3 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image6)
            }
            correctDiff == 4 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image7)
            }
            correctDiff > 4 -> {
                holder.userFigure.setImageResource(R.drawable.figure_image8)
            }
        }
        when(position){
            0 -> {
                holder.rankMedal.setImageResource(R.drawable.baseline_medal_gold)
            }
            1 -> {
                holder.rankMedal.setImageResource(R.drawable.baseline_medal_silver)
            }
            2 -> {
                holder.rankMedal.setImageResource(R.drawable.baseline_medal_bronze)
            }
            else -> {
                holder.rankMedal.visibility = View.GONE
                holder.rankText.visibility = View.VISIBLE
                holder.rankText.text = position.toString()
            }
        }
        val name = membersList[position].userName.substring(0,3)
        holder.userNameWithPoints.text = name + ": " + membersList[position].userScore + "分"
        if(membersList[position].userName == Constants.userId){
            holder.userNameWithPoints.text = name + "(你): " + membersList[position].userScore + "分"
        }

    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val userFigure: ImageView = itemView.findViewById(R.id.user_figure)
        val userNameWithPoints: TextView = itemView.findViewById(R.id.user_name_and_points)
        val rankMedal: ImageButton = itemView.findViewById(R.id.rank_medal)
        val rankText: TextView = itemView.findViewById(R.id.rank_text)
    }
}