package com.example.quizbanktest.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
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

    }
}