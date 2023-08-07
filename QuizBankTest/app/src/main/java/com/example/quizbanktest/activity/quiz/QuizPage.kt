package com.example.quizbanktest.activity.quiz

import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout



import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.adapters.quiz.QuizPageAdapter
import com.example.quizbanktest.databinding.ActivityQuizPageBinding
import com.example.quizbanktest.models.Question


class QuizPage: BaseActivity() {
    private lateinit var quizPageBinding: ActivityQuizPageBinding
    private lateinit var fragmentAdapter: QuizPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizPageBinding = ActivityQuizPageBinding.inflate(layoutInflater)
        setContentView(quizPageBinding.root)

        quizPageBinding.quizAdd.setOnClickListener {  }
        quizPageBinding.quizRecord.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecordPage::class.java)
            startActivity(intent)
        }
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

        setupNavigationView()
        doubleCheckExit()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("back in Quiz Page request code=", requestCode.toString())
    }

}