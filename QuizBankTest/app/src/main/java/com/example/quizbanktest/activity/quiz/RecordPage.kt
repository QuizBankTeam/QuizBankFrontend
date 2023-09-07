package com.example.quizbanktest.activity.quiz
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.RecordPageAdapter
import com.example.quizbanktest.databinding.ActivityRecordPageBinding
import com.example.quizbanktest.fragment.MultiRecordPage
import com.example.quizbanktest.fragment.SingleRecordPage
import com.google.android.material.tabs.TabLayout


class RecordPage: AppCompatActivity() {
    private lateinit var recordBinding: ActivityRecordPageBinding
    private lateinit var fragmentAdapter: RecordPageAdapter
    private lateinit var SRPFragment: SingleRecordPage
    private lateinit var MRPFragment: MultiRecordPage
    private var currentPosition = 0
    private var deleteBtnVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recordBinding = ActivityRecordPageBinding.inflate(layoutInflater)
        setContentView(recordBinding.root)

        recordBinding.backBtn.setOnClickListener {
            finish()
        }

        SRPFragment = SingleRecordPage()
        MRPFragment = MultiRecordPage()
        fragmentAdapter = RecordPageAdapter(supportFragmentManager, lifecycle, SRPFragment, MRPFragment)
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
                currentPosition = position
                recordBinding.selectRecordMode.selectTab(recordBinding.selectRecordMode.getTabAt(position))
            }
        })
        recordBinding.QuizRecordDelete.setOnClickListener {
            if(deleteBtnVisible){
                recordBinding.QuizRecordDelete.setImageResource(R.drawable.baseline_trash_bin)
                deleteBtnVisible = false
                fragmentAdapter.getSRPFragment().onDeleteBtnClick(false)
            }else{
                recordBinding.QuizRecordDelete.setImageResource(R.drawable.baseline_cancel_24)
                deleteBtnVisible = true

                if(currentPosition==0){
                    fragmentAdapter.getSRPFragment().onDeleteBtnClick(true)
                }else{
                    Toast.makeText(this, "多人考試目前無法瀏覽", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    override fun onBackPressed() {
        finish()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun doubleCheckExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                finish()
            }
        }
    }
}