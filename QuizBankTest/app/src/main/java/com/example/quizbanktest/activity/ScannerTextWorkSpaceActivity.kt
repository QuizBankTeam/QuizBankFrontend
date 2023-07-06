package com.example.quizbanktest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.OcrResultViewAdapter
import com.example.quizbanktest.models.OcrResultModel
import com.example.quizbanktest.utils.ConstantsAccountServiceFunction

class ScannerTextWorkSpaceActivity : BaseActivity() {
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

        var homeButton : ImageButton  = findViewById(R.id.home)
        homeButton.setOnClickListener{
            gotoHomeActivity()
        }
        var bank : ImageButton = findViewById(R.id.bank)
        bank.setOnClickListener{
            gotoBankActivity()
        }

        var camera : ImageButton = findViewById(R.id.camera)
        camera?.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(
                pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    // Here we have create the methods for image selection from GALLERY
                    0 -> choosePhotoToOcr()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
        toolBar.setNavigationOnClickListener{
            Log.e("nav","toolbar")
            var builder =AlertDialog.Builder(this)
                .setMessage(" 您確定要離開嗎系統不會保存這次修改喔 ")
                .setTitle("OCR結果")
                .setIcon(R.drawable.baseline_warning_amber_24)
            builder.setPositiveButton("確認") { dialog, which ->
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

            builder.setNegativeButton("取消") { dialog, which ->

            }
            builder.show()

        }
        ConstantsAccountServiceFunction.getCsrfToken(this@ScannerTextWorkSpaceActivity)
        ConstantsAccountServiceFunction.login(this@ScannerTextWorkSpaceActivity)

    }

    private fun setupOcrRecyclerView(ocrResultList: ArrayList<OcrResultModel>) {
        var ocrList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.ocr_list)
        ocrList?.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        ocrList?.setHasFixedSize(true)

        val placesAdapter = OcrResultViewAdapter(this@ScannerTextWorkSpaceActivity,this, ocrResultList)
        ocrList?.adapter = placesAdapter
    }


}