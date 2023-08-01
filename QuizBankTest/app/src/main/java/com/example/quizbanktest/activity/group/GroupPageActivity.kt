package com.example.quizbanktest.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.bank.BankActivity
import de.hdodenhof.circleimageview.CircleImageView

class GroupPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        val backArrowBtn: ImageButton = findViewById(R.id.btn_group_page_back_arrow)
        val groupTitle = intent.getStringExtra("group_name").toString()
        val memberList = intent.getStringArrayListExtra("members")
        val imageView = findViewById<CircleImageView>(R.id.iv_user_image)
        imageView.setOnClickListener {
            // 顯示目前群組傳過的圖片
        }

        backArrowBtn.setOnClickListener {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(groupTitle.isNotEmpty()){
            val groupName: TextView = findViewById(R.id.group_name)
            groupName.text = groupTitle
        }

        val chatButton = findViewById<ImageButton>(R.id.group_chatroom)
        chatButton.setOnClickListener {
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("chatroom_title",groupTitle+"聊天室")
            startActivity(intent)
            finish()
        }

        val fileButton = findViewById<ImageButton>(R.id.group_file)
        fileButton.setOnClickListener {
            // 顯示傳過的檔案
        }

        val imageButton = findViewById<ImageButton>(R.id.group_image)
        imageButton.setOnClickListener {
            // Handle image button click
        }

    }



}