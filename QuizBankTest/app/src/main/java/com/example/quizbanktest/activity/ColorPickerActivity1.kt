package com.example.quizbanktest.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.R
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.AlphaSlideBar


class ColorPickerActivity1 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker1)
        val colorPickerView = findViewById<ColorPickerView>(R.id.colorPickerView)
        val textView: TextView = findViewById(R.id.text)
        val imageView: ImageView = findViewById(R.id.colorShow)
        var hexCode :String ?=null
        var colorGet :Int ?=null
        val imagePath = intent.getStringExtra("bitmap_image")

        val bitmap = BitmapFactory.decodeFile(imagePath)

        val drawable = BitmapDrawable(resources, bitmap)
        //手機不能用 但模擬器可以
//        val alphaSlideBar = findViewById<AlphaSlideBar>(R.id.alphaSlideBar)
//        colorPickerView.attachAlphaSlider(alphaSlideBar)
        colorPickerView.setPaletteDrawable(drawable)
        colorPickerView.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
            textView.setText("#" + envelope.getHexCode())
            hexCode = envelope.getHexCode()
            imageView.setBackgroundColor(envelope.getColor())
            colorGet = envelope.getColor()
        })
        val checkButton : ImageView  =findViewById(R.id.chooseButton)
        checkButton.setOnClickListener{

            val intent = Intent()
            intent.putExtra("colorHexCode",hexCode)
            intent.putExtra("colorPick",colorGet)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }
}