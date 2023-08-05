package com.example.quizbanktest.activity.group

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat

import com.example.quizbanktest.R

import de.hdodenhof.circleimageview.CircleImageView



class GroupPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_page)
        val backArrowBtn: ImageButton = findViewById(R.id.btn_group_page_back_arrow)
        val groupTitle = intent.getStringExtra("group_name").toString()
        val memberList = intent.getStringArrayListExtra("members")
        val imageView = findViewById<CircleImageView>(R.id.iv_group_image)
        imageView.setOnClickListener {
            // 顯示目前群組的大頭貼
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
            // 顯示群組題庫
        }

        val recordButton = findViewById<ImageButton>(R.id.group_quiz_cord)
        recordButton.setOnClickListener {
            // 顯示考試紀錄
        }
        val settingButton = findViewById<ImageButton>(R.id.group_page_setting)
        settingButton.setOnClickListener {
            val intent = Intent(this, GroupSettingActivity::class.java)
            intent.putExtra("setting_title",groupTitle+"設定")
            startActivity(intent)
            finish()
        }

        val addPersonButton = findViewById<ImageButton>(R.id.group_add_person)
        addPersonButton.setOnClickListener {
            val inviteDialog = Dialog(this)
            inviteDialog.setContentView(R.layout.dialog_give_group_invite_code)
            inviteDialog.show()
            val textView = inviteDialog.findViewById<TextView>(R.id.join_group_invite_code)
            val btnCopy = inviteDialog.findViewById<Button>(R.id.btn_copy)
            btnCopy.setOnClickListener {
                val textToCopy = textView.text
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", textToCopy)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@GroupPageActivity, "邀請碼已复制", Toast.LENGTH_SHORT).show()
            }
        }
        PullExit()

    }

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