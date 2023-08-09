package com.example.quizbanktest.activity.group

import android.annotation.SuppressLint
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
        val addPersonButton = findViewById<ImageButton>(R.id.group_add_person)
        addPersonButton.setOnClickListener {
            val inviteDialog = Dialog(this)
            inviteDialog.setContentView(R.layout.dialog_give_group_invite_code)
            inviteDialog.show()
            val textView = inviteDialog.findViewById<TextView>(R.id.join_group_invite_code)
            val btnCopy = inviteDialog.findViewById<Button>(R.id.btn_copy)
            btnCopy.setOnClickListener {
                val textToCopy = textView.hint
                Log.e("textview",textView.text.toString())
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", textToCopy)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@GroupSettingActivity, "邀請碼已复制", Toast.LENGTH_SHORT).show()
            }
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