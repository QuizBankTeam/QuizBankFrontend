package com.example.quizbanktest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.OcrResultViewAdapter
import com.example.quizbanktest.models.OcrResultModel
import com.example.quizbanktest.utils.ConstantsServiceFunction

class ScannerTextWorkSpaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_text_work_space)
        setupOcrRecyclerView(ConstantsOcrResults.getQuestions())
        var toolBar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_ocr_detail)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            Log.e("in action bar","not null")
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            Log.e("nav","toolbar")
        }

        toolBar.setNavigationOnClickListener{
            Log.e("nav","toolbar")
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        ConstantsServiceFunction.getCsrfToken(this@ScannerTextWorkSpaceActivity)
        ConstantsServiceFunction.login(this@ScannerTextWorkSpaceActivity)

    }

    private fun setupOcrRecyclerView(ocrResultList: ArrayList<OcrResultModel>) {
        var ocrList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.ocr_list)
        ocrList?.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        ocrList?.setHasFixedSize(true)
        Log.e("ocrText",ConstantsOcrResults.getQuestions()[0].description)
        val placesAdapter = OcrResultViewAdapter(this, ocrResultList)
        ocrList?.adapter = placesAdapter
    }


}