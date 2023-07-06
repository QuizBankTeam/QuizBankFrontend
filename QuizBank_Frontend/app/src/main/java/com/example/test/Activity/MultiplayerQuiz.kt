package com.example.test.Adapter

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.test.R
import com.example.test.model.Option

class OptionAdapter1(private val context: Activity, private  val arrayList: ArrayList<Option>) :
    ArrayAdapter<Option>(context, R.layout.option_row, arrayList) {
    private var answerOptions : ArrayList<Int> = ArrayList()
    private var inStartQuiz: Boolean = false
    private var inRecord: Boolean = false
    private var selectOption: ArrayList<Int> = ArrayList()
    private var answerIsCorrect: Boolean = false
    fun setAnswerOptions(answer: ArrayList<Int>)
    {
        answerOptions = answer
        notifyDataSetChanged()
    }
    fun setInStartQuiz(inQuiz: Boolean){
        inStartQuiz = inQuiz
    }
    fun setRecord(inRecord: Boolean, answerIsCorrect: Boolean, selectOptions: ArrayList<Int>){
        this.selectOption = selectOptions
        this.inRecord = inRecord
        this.answerIsCorrect = answerIsCorrect
        this.inStartQuiz = true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.option_row, null)

        val optionnum : TextView = view.findViewById(R.id.optionNum)
        val optioncontent : TextView = view.findViewById(R.id.optionContent)
        val optionBackground : LinearLayout = view.findViewById(R.id.option_background)

        optionnum.text = arrayList[position].optionNum
        optioncontent.text = arrayList[position].optionContent
        if(!inStartQuiz){
            for (item in answerOptions)
            {
                if(item==position)
                {
                    optionBackground.setBackgroundColor(Color.parseColor("#c6fa73"))
                }
            }
            for(item in selectOption)
            {
                for (item2 in answerOptions)
                {
                    if(item!=item2)
                    {
                        optionBackground.setBackgroundColor(Color.parseColor("##CE0000"))
                    }
                }
            }
        }
        if(inRecord){
            for (item in answerOptions)
            {
                if(item==position)
                {
                    optionnum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check,0, 0, 0)
                }
            }
            for(item in selectOption)
            {
                if(item==position && !answerOptions.contains(item))
                {
                    optioncontent.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.baseline_edit, 0)
                }
            }
            if(position == arrayList.size-1){
                optionBackground.removeView(optionnum)
                optioncontent.gravity = Gravity.CENTER
                if(answerIsCorrect){
                    optionBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.light_red2))
                }else{
                    optionBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_correct))
                }
            }
        }
        return view
    }

}