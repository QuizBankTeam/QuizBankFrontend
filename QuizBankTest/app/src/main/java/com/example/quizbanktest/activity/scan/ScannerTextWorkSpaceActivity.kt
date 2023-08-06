package com.example.quizbanktest.activity.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.adapters.scan.OcrResultViewAdapter
import com.example.quizbanktest.models.QuestionModel


class ScannerTextWorkSpaceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_text_work_space)
        setupOcrRecyclerView(ConstantsOcrResults.getOcrResult())
        val emptyShow : LinearLayout = findViewById(R.id.empty_show)
        emptyShow.visibility = View.GONE

        val showOcrResult : LinearLayout = findViewById(R.id.ocr_result)
        showOcrResult.visibility = View.VISIBLE

        val enterToTal : LinearLayout = findViewById(R.id.enter_buttons)
        enterToTal.visibility = View.VISIBLE

        if(ConstantsOcrResults.getOcrResult().size ==0){
            val emptyShow : LinearLayout = findViewById(R.id.empty_show)
            emptyShow.visibility = View.VISIBLE
            showOcrResult.visibility = View.GONE
            enterToTal.visibility = View.GONE
        }

        val toolBar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_ocr_detail)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

       setupNavigationView()

        toolBar.setNavigationOnClickListener{
            if(ConstantsOcrResults.getOcrResult().size!=0){
                Log.e("nav","toolbar")
                val builder =AlertDialog.Builder(this)
                    .setMessage(" 您確定要離開嗎系統不會保存這次修改喔 ")
                    .setTitle("OCR結果")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                builder.setNegativeButton("取消") { dialog, which ->

                }
                builder.show()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        doubleCheckExit()

    }

    private fun setupOcrRecyclerView(ocrResultList: ArrayList<QuestionModel>) {
        val ocrList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.ocr_list)
        ocrList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        ocrList.setHasFixedSize(true)

        val placesAdapter = OcrResultViewAdapter(this@ScannerTextWorkSpaceActivity,this, ocrResultList)
        ocrList.adapter = placesAdapter
    }

}