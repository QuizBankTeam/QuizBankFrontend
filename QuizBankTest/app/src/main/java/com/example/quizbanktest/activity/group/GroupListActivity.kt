package com.example.quizbanktest.activity.group

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.bank.BankActivity
import com.example.quizbanktest.adapters.group.GroupListAdapter
import com.example.quizbanktest.models.GroupModel
import com.example.quizbanktest.utils.group.ConstantsGroup

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
        val addGroupBtn:ImageButton = findViewById(R.id.group_add)

        addGroupBtn.setOnClickListener {

            val actionDialog = AlertDialog.Builder(this)
            actionDialog.setTitle("群組功能選擇")
            val actionDialogItems =
                arrayOf("加入群組", "創建群組")
            actionDialog.setItems(
                actionDialogItems
            ) { dialog, which ->
                when (which) {
                    // Here we have create the methods for image selection from GALLERY
                    0 -> joinGroup()
                    1 -> createGroup()
                }
            }
            actionDialog.show()

        }

        setupGroupListRecyclerView(ConstantsGroup.groupList)
    }

    fun createGroup(){
        val groupDialog = Dialog(this)
        groupDialog.setContentView(R.layout.dalog_create_group)
        groupDialog.setTitle("群組")
        groupDialog.show()
        val groupCreateBtn = groupDialog.findViewById<TextView>(R.id.group_enter)
        groupCreateBtn.setOnClickListener {
            val create_group_name =  groupDialog.findViewById<EditText>(R.id.iv_create_group_name)
            if (create_group_name.text.toString().trim().isNotEmpty()) {
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
                    Toast.makeText(this@GroupListActivity, "邀請碼已复制", Toast.LENGTH_SHORT).show()
                    inviteDialog.dismiss()
                }
                groupDialog.dismiss()
            } else {
                // 如果 create_group_name 是空的，則彈出一個提示
                Toast.makeText(this@GroupListActivity, "請先輸入群組名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun joinGroup(){
        val groupDialog = Dialog(this)
        groupDialog.setContentView(R.layout.dialog_join_group)
        groupDialog.setTitle("輸入群組邀請碼")
        groupDialog.show()
    }

    private fun setupGroupListRecyclerView(groupList: ArrayList<GroupModel>) {
        val groupListView : RecyclerView = findViewById(R.id.groupListRecyclerView)
        val groupAdapter = GroupListAdapter(this, groupList)

        groupListView.adapter = groupAdapter
        groupListView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)


    }
}