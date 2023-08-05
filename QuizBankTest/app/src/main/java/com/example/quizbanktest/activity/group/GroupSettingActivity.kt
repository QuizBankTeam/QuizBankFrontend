package com.example.quizbanktest.activity.group

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import com.example.quizbanktest.R
import de.hdodenhof.circleimageview.CircleImageView

class GroupSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_setting)

        val backArrowBtn: ImageButton = findViewById(R.id.btn_group_setting_back_arrow)


        backArrowBtn.setOnClickListener {
            val intent = Intent(this, GroupPageActivity::class.java)
            startActivity(intent)
            finish()
        }



        PullExit()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun PullExit(){
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                doubleBackToExit()
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    private fun doubleBackToExit() {
        val intent = Intent(this, GroupListActivity::class.java)
        startActivity(intent)
        finish()
    }



    override fun onBackPressed() {
        val intent = Intent(this, GroupListActivity::class.java)
        startActivity(intent)
        finish()
    }
}