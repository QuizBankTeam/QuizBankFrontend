package com.example.yiquizapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yiquizapp.*
import com.example.yiquizapp.adapter.BankRecyclerViewAdapter
import com.example.yiquizapp.interfaces.RecyclerViewInterface
import com.example.yiquizapp.models.BankModel
import com.example.yiquizapp.view.WrapLayout
import jp.wasabeef.blurry.Blurry


class MainActivity : AppCompatActivity(), RecyclerViewInterface {

    lateinit var searchView: SearchView
    lateinit var menuButton: ImageButton
    lateinit var view: LinearLayout
    private var wrapLayout: WrapLayout? = null
    private var blurred = false

    var bankModels = ArrayList<BankModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView : RecyclerView = findViewById(R.id.mRecyclerView)
        val bankRecyclerViewAdapter = BankRecyclerViewAdapter(this, bankModels, this)

        setupBankModels()
        recyclerView.adapter = bankRecyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


    }

    private fun setupBankModels() {
        var bankNames : Array<String> = resources.getStringArray(R.array.bank_name_txt)
        var bankDescriptions : Array<String> = resources.getStringArray(R.array.bank_description_txt)
        var bankDates : Array<String> = resources.getStringArray(R.array.bank_date_txt)

        for (i in bankNames.indices) {
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
            Blurry.with(this@MainActivity)
                .radius(25)
                .sampling(2)
                .async()
                .animate(200)
                .onto(findViewById<View>(R.id.content) as ViewGroup)
            Log.d(getString(R.string.app_name),
                "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

        val popupInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myContentView = popupInflater.inflate(R.layout.popup_window, null)
        myContentView.measure(View.MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
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
        for (element in strs) {
            val itemLayout = popupInflater.inflate(R.layout.layout_item, wrapLayout, false)
            val name = itemLayout.findViewById<View>(R.id.name) as TextView
            name.text = element
            wrapLayout!!.addView(itemLayout)
        }

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        myContentView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()     /* It will dismiss the popup window when tapped in it */
            return@setOnTouchListener true
        }

    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, BankMainActivity:: class.java)

        intent.putExtra("NAME", bankModels.get(position).bankName)

        startActivity(intent)
        overridePendingTransition(R.anim.main_to_bank_out, R.anim.main_to_bank_in);
    }


}
