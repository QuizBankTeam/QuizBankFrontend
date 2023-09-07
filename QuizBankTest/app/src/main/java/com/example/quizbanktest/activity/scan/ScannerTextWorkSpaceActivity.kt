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
import com.example.introducemyself.utils.ConstantsOcrResults.questionTypeSpinner
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.activity.MainActivity
import com.example.quizbanktest.adapters.scan.OcrResultViewAdapter
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.models.ScanQuestionModel


class ScannerTextWorkSpaceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scanner_text_work_space)
        initList()
        val intent = intent
        val optionsBundle = intent.getBundleExtra("options")
//        autoCutOptions()
        val first = optionsBundle?.getString("first")
        val second = optionsBundle?.getStringArrayList("second")
        val optionsPair: Pair<String, List<String>>? = if (first != null && second != null) {
            Pair(first, second)
        } else {
            null
        }
        setupOcrRecyclerView(ConstantsOcrResults.getOcrResult(),optionsPair)
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

       val backButton : ImageButton = findViewById(R.id.scan_back)

       setupNavigationView()

        backButton.setOnClickListener{
            if(ConstantsOcrResults.getOcrResult().size!=0){
                Log.e("nav","toolbar")
                val builder =AlertDialog.Builder(this,R.style.CustomAlertDialogStyle)
                    .setMessage(" 您確定要離開嗎系統不會保存這次修改喔 ")
                    .setTitle("掃描修改")
                    .setIcon(R.drawable.baseline_warning_amber_24)
                builder.setPositiveButton("確認") { dialog, which ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                builder.setNegativeButton("取消") { dialog, which ->

                }
                builder.show()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val addEmptyButton : ImageButton = findViewById(R.id.add_empty_scan_result)

        addEmptyButton.setOnClickListener {
            ConstantsOcrResults.addEmptyScanResult()
            if(ConstantsOcrResults.getOcrResult().size ==1){
                val intent = Intent(this, ScannerTextWorkSpaceActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val ocrList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.ocr_list)
                val newPosition = ConstantsOcrResults.getOcrResult().size - 1
                ocrList.adapter?.notifyItemInserted(newPosition)
            }

        }

        val deleteButton : ImageButton = findViewById(R.id.delete_all_scan_result)

        deleteButton.setOnClickListener {
            val builder =AlertDialog.Builder(this,R.style.CustomAlertDialogStyle)
                .setMessage(" 您確定要刪除所有的暫存掃描結果嗎 ")
                .setTitle("掃描結果")
                .setIcon(R.drawable.baseline_warning_amber_24)
            builder.setPositiveButton("確認") { dialog, which ->
                ConstantsOcrResults.questionList.clear()
                val intent = Intent(this, ScannerTextWorkSpaceActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("取消") { dialog, which ->

            }
            builder.show()
        }

        doubleCheckExit()

    }
    private fun initList() {
        if(questionTypeSpinner.size>0){}
        else{
            questionTypeSpinner.add(QuestionTypeItem("填充題", R.drawable.crocodile))
            questionTypeSpinner.add(QuestionTypeItem("單選題", R.drawable.fox))
            questionTypeSpinner.add(QuestionTypeItem("簡答題", R.drawable.giraffe))
            questionTypeSpinner.add(QuestionTypeItem("多選題", R.drawable.hedgehog))
            questionTypeSpinner.add(QuestionTypeItem("是非題", R.drawable.lion))
        }
    }
    private fun setupOcrRecyclerView(ocrResultList: ArrayList<ScanQuestionModel>, optionList :  Pair<String, List<String>>?) {
        val ocrList : androidx.recyclerview.widget.RecyclerView = findViewById(R.id.ocr_list)
        ocrList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        ocrList.setHasFixedSize(true)

        val placesAdapter = OcrResultViewAdapter(this@ScannerTextWorkSpaceActivity,this, ocrResultList,optionList)
        ocrList.adapter = placesAdapter
    }

}