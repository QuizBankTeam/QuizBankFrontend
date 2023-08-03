package com.example.quizbanktest.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.core.os.BuildCompat
import com.example.quizbanktest.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        val backArrowBtn: ImageButton = findViewById(R.id.chat_room_back)
        val roomTitle = intent.getStringExtra("chatroom_title").toString()



        backArrowBtn.setOnClickListener {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(roomTitle.isNotEmpty()){
            val roomName: TextView = findViewById(R.id.chatroom_name)
            roomName.text = roomTitle
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