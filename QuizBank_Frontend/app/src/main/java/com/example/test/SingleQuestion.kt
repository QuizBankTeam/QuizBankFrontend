package com.example.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.ActivityMainBinding
import com.example.test.databinding.MpQuizBinding
import com.example.test.databinding.SingleQuestionBinding

class SingleQuestion : AppCompatActivity(){
    private lateinit var questionBinding: SingleQuestionBinding
    private  var optionlist : ArrayList<Option> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionBinding = SingleQuestionBinding.inflate(layoutInflater)
        setContentView(questionBinding.root)
        init()
        questionBinding.QuestionOption.adapter = OptionAdapter(this, optionlist)
        questionBinding.backBtn.setOnClickListener { finish() }
    }

    private fun init()
    {
        val optionText = arrayOf("A demo text 123", "B demo text 456", "C demo text 789", "D demo text 101112")
        val optionNum = arrayOf("A", "B", "C", "D")
        for(i in optionNum.indices)
        {
            val tmpOption = Option(optionNum[i], optionText[i])
            optionlist.add(tmpOption)
        }
    }
}