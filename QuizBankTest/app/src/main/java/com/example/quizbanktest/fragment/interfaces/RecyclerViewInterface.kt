package com.example.quizbanktest.fragment.interfaces

import android.widget.TextView

interface RecyclerViewInterface {
    fun onItemClick(position: Int)

    fun switchBank(position: Int)

    fun settingCard()

}