package com.example.test

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class OptionAdapter(private val context: Activity, private  val arrayList: ArrayList<Option>) :
    ArrayAdapter<Option>(context, R.layout.option_row, arrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.option_row, null)

        val optionnum : TextView = view.findViewById(R.id.optionNum)
        val optioncontent : TextView = view.findViewById(R.id.optionContent)

        optionnum.text = arrayList[position].optionNum
        optioncontent.text = arrayList[position].optionContent

        return view
    }
}