package com.example.test.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.test.Fragment.MultiQuizPage
import com.example.test.Fragment.MultiRecordPage
import com.example.test.Fragment.SingleQuizPage
import com.example.test.Fragment.SingleRecordPage

class RecordPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position==0) {
            SingleRecordPage()
        } else {
            MultiRecordPage()
        }
    }

}