package com.example.quizbanktest.fragment.interfaces

import android.widget.TextView

interface RecyclerViewInterface {
    fun onItemClick(position: Int)

    fun getAnswerOptionPosition(position: Int)
}