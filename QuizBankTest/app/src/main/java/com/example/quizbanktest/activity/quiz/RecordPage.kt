package com.example.quizbanktest.activity.quiz
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.quizbanktest.adapters.quiz.RecordPageAdapter
import com.example.quizbanktest.databinding.ActivityRecordPageBinding
import com.google.android.material.tabs.TabLayout


class RecordPage: AppCompatActivity() {
    private lateinit var recordBinding: ActivityRecordPageBinding
    private lateinit var fragmentAdapter: RecordPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordBinding = ActivityRecordPageBinding.inflate(layoutInflater)
        setContentView(recordBinding.root)

        fragmentAdapter = RecordPageAdapter(supportFragmentManager, lifecycle)
        recordBinding.selectRecordMode.addTab( recordBinding.selectRecordMode.newTab().setText("單人") )
        recordBinding.selectRecordMode.addTab( recordBinding.selectRecordMode.newTab().setText("多人") )

        recordBinding.recordPager.adapter = fragmentAdapter


        recordBinding.selectRecordMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    recordBinding.recordPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        recordBinding.recordPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                recordBinding.selectRecordMode.selectTab(recordBinding.selectRecordMode.getTabAt(position))
            }
        })

        init()
        Log.d("record page is created", "")
    }

    private fun init(){

    }
}