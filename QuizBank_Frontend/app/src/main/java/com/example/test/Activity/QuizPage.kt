package com.example.test.Activity

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test.Adapter.QuizPageAdapter
import com.example.test.databinding.ActivityMainBinding
import com.example.test.databinding.QuizPageBinding

class QuizPage: AppCompatActivity() {
    private lateinit var quizPageBinding: QuizPageBinding
    private lateinit var fragmentAdapter: QuizPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizPageBinding = QuizPageBinding.inflate(layoutInflater)
        setContentView(quizPageBinding.root)

        fragmentAdapter = QuizPageAdapter(supportFragmentManager, lifecycle)
        quizPageBinding.selectQuizMode.addTab(quizPageBinding.selectQuizMode.newTab().setText("單人"))
        quizPageBinding.selectQuizMode.addTab(quizPageBinding.selectQuizMode.newTab().setText("多人"))

        quizPageBinding.quizPager.adapter = fragmentAdapter

        quizPageBinding.selectQuizMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    quizPageBinding.quizPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        quizPageBinding.quizPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                quizPageBinding.selectQuizMode.selectTab(quizPageBinding.selectQuizMode.getTabAt(position))
            }
        })

    }
}