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
    private lateinit var SPFragment: SingleQuizPage
    private lateinit var MPFragment: MultiQuizPage
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        SPFragment = SingleQuizPage()
        MPFragment = MultiQuizPage()
        return if(position==0) {
            SPFragment
        } else {
            MPFragment
        }
    }
    fun getSPFragment(): SingleQuizPage {
        return SPFragment
    }
    fun getMPFragment(): MultiQuizPage {
        return MPFragment
    }
}