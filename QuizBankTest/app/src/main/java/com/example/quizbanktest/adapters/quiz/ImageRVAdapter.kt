package com.example.quizbanktest.adapters.quiz

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.models.Quiz

class ImageRVAdapter(private val context: Activity, private val imageList: ArrayList<Bitmap>):
    RecyclerView.Adapter<ImageRVAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_quiz_question_image, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.image.setImageBitmap(imageList[position])
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val image: ImageView = itemView.findViewById(R.id.question_image)
    }
}