package com.example.quizbanktest.activity.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.bank.BankActivity

class GroupListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        setupNavigationView()
        doubleCheckExit()
        val backArrowBtn:ImageButton = findViewById(R.id.btn_group_back_arrow)

        backArrowBtn.setOnClickListener {
            val intent = Intent(this,BankActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}