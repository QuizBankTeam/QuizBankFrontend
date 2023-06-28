package com.example.test.Adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.test.R
import com.example.test.model.Option

class OptionAdapter(private val context: Activity, private  val arrayList: ArrayList<Option>) :
    ArrayAdapter<Option>(context, R.layout.option_row, arrayList) {
    private var answerOptions : ArrayList<Int> = ArrayList()
    fun setAnswerOptions(answer: ArrayList<Int>)
    {
        answerOptions = answer
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.option_row, null)

        val optionnum : TextView = view.findViewById(R.id.optionNum)
        val optioncontent : TextView = view.findViewById(R.id.optionContent)

        optionnum.text = arrayList[position].optionNum
        optioncontent.text = arrayList[position].optionContent
        for (item in answerOptions)
        {
            if(item==position)
            {
                val optionBackground : LinearLayout = view.findViewById(R.id.option_background)
                optionBackground.setBackgroundColor(Color.parseColor("#c6fa73"))
            }
        }
        return view
    }

}