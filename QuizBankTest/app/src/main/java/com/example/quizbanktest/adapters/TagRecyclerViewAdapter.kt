package com.example.quizbanktest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import com.example.quizbanktest.R

class TagRecyclerViewAdapter(private val mContext: Context, private val mData: Array<String>) :
    RecyclerView.Adapter<TagRecyclerViewAdapter.TagViewHolder>() {
    private var mOnClickListener: View.OnClickListener? = null
    override fun getItemCount(): Int {
        return 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.item_tag, parent, false), mOnClickListener
        )
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.tagContainerLayout.setTags(*mData)
        holder.button.setOnClickListener(mOnClickListener)
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        mOnClickListener = listener
    }

    inner class TagViewHolder(v: View, var clickListener: View.OnClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        var tagContainerLayout: TagContainerLayout
        var button: Button

        init {
            tagContainerLayout =
                v.findViewById<View>(R.id.tagcontainerLayout) as TagContainerLayout
            button = v.findViewById<View>(R.id.button) as Button

        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v)
            }
        }
    }
}