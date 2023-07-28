package com.example.quizbanktest.adapters.quiz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.MultiQuizPage
import com.example.quizbanktest.fragment.SingleQuizPage

class QuizPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position==0) {
            SingleQuizPage()
        } else {
            MultiQuizPage()
        }
    }

}