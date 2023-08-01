package com.example.quizbanktest.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.bank.BankActivity

class GroupPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        val backArrowBtn: ImageButton = findViewById(R.id.btn_group_page_back_arrow)
        val groupTitle = intent.getStringExtra("group_name").toString()
        backArrowBtn.setOnClickListener {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(groupTitle.isNotEmpty()){
            val groupName: TextView = findViewById(R.id.group_name)
            groupName.text = groupTitle
        }
    }



}