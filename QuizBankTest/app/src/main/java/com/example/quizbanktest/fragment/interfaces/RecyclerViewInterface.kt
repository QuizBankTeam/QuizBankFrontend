package com.example.quizbanktest.fragment.interfaces

import android.widget.TextView

interface RecyclerViewInterface {
    fun onItemClick(position: Int)
    fun switchBank(newBankPosition: Int)
    fun settingCard(position: Int)
    fun updateOption(position: Int, newOption: String)

}