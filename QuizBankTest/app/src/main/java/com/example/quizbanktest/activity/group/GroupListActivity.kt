package com.example.quizbanktest.activity.group

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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
    }

    fun joinGroup(){
        val groupDialog = Dialog(this)
        groupDialog.setContentView(R.layout.dialog_join_group)
        groupDialog.setTitle("輸入群組邀請碼")
        groupDialog.show()
    }

    private fun setupGroupListRecyclerView(groupList: ArrayList<GroupModel>) {
        val groupListView : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.groupListRecyclerView)
        groupListView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        val placesAdapter = GroupListAdapter(this, groupList)
        groupListView.adapter = placesAdapter

    }
}