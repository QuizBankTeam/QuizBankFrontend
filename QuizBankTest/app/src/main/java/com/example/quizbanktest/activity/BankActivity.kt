package com.example.quizbanktest.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.introducemyself.utils.ConstantsOcrResults
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.BankRecyclerViewAdapter

import com.example.quizbanktest.models.BankModel

import com.example.quizbanktest.view.WrapLayout
import jp.wasabeef.blurry.Blurry

class BankActivity : BaseActivity() {
    lateinit var searchView: SearchView
    lateinit var menuButton: ImageButton
    private var wrapLayout: WrapLayout? = null
    private var blurred = false
    var bankModels = ArrayList<BankModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        val toolBar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_home_detail)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            Log.e("in action bar","not null")
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            Log.e("nav","toolbar")
        }
        toolBar.setNavigationOnClickListener{

           val intent = Intent(this,MainActivity::class.java)
           startActivity(intent)

        }
        val camera : ImageButton = findViewById(R.id.camera)
        camera.setOnClickListener {
            cameraPick()
        }

        val recyclerView : RecyclerView = findViewById(R.id.mRecyclerView)
        val homeButton : ImageButton  = findViewById(R.id.home)
        homeButton.setOnClickListener{
            gotoHomeActivity()
        }
        val adapter = BankRecyclerViewAdapter(this, bankModels)
        setUpBankModels()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    private fun setUpBankModels() {
        val bankNames : Array<String> = resources.getStringArray(R.array.bank_name_txt)
        val bankDescriptions : Array<String> = resources.getStringArray(R.array.bank_description_txt)
        val bankDates : Array<String> = resources.getStringArray(R.array.bank_date_txt)

        for (i in 0 until bankNames.size) {
            val bankModel = BankModel(bankNames[i], bankDescriptions[i], bankDates[i], i)
            bankModels.add(bankModel)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setPopupWindow(view: View?) {

        if (blurred) {
            blurred = false
            Blurry.delete(findViewById(R.id.content))
        } else {
            blurred = true
            val startMs = System.currentTimeMillis()
            Blurry.with(this@BankActivity)
                .radius(25)
                .sampling(2)
                .async()
                .animate(200)
                .onto(findViewById<View>(R.id.content) as ViewGroup)
            Log.d(getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

//        blurred = !blurred
//        return true

        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = myContentView.measuredHeight

        // popup window set up
        val popupWindow = PopupWindow(this).apply {
            contentView = myContentView
//            width = ViewGroup.LayoutParams.MATCH_PARENT
            width = 1400
            height = 1600
            animationStyle = R.style.PopupAnimation
            isFocusable = true
            isOutsideTouchable = false
            isClippingEnabled = false
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            if (blurred) {
                blurred = false
                Blurry.delete(findViewById(R.id.content))
            }

        })

        // automatically newline tags view
        wrapLayout = myContentView.findViewById(R.id.clip_layout)
        val strs = arrayOf(
            "奔跑吧兄弟",
            "running man",
            "笑傲江湖",
            "快樂大本營",
            "維多利亞的秘密",
            "非誠勿擾",
            "康熙來了",
            "123"
        )
        for (i in 0 until strs.size) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = strs[i]
            wrapLayout!!.addView(itemLayout)
        }

        popupWindow.showAtLocation(view, Gravity.CENTER,     0, 0)
        myContentView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()     /* It will dismiss the popup window when tapped in it */
            return@setOnTouchListener true
        }

    }
}