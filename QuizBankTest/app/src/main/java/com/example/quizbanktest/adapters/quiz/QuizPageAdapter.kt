package com.example.quizbanktest.adapters.quiz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.MultiQuizPage
import com.example.quizbanktest.fragment.SingleQuizPage

class QuizPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, SPFragment: SingleQuizPage, MPFragment: MultiQuizPage) :
    FragmentStateAdapter(fragmentManager, lifecycle){
    private val Spf = SPFragment
    private val Mpf = MPFragment

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position==0) {
            Spf
        } else {
            Mpf
        }
    }
    fun getSPFragment(): SingleQuizPage {
        return Spf
    }
    fun getMPFragment(): MultiQuizPage {
        return Mpf
    }
}