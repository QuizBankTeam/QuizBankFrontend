package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        init()


        binding.loginbutton.setOnClickListener{ buttonClick() }
    }

    private fun buttonClick(){
        var email : String = binding.account.text.toString()
        var password : String = binding.password.text.toString()
        val intent = Intent()
        intent.setClass(this@MainActivity, MultiplayerQuiz::class.java)
        intent.putExtra("Key_email", email)
        intent.putExtra("Key_password", password)
        startActivity(intent)

    }
//    private fun init()
//    {
//        val optionText = arrayOf("A demo text 123", "B demo text 456", "C demo text 789", "D demo text 101112")
//        val optionNum = arrayOf("A", "B", "C", "D")
//        for(i in optionNum.indices)
//        {
//            val tmpOption = Option(optionNum[i], optionText[i])
//            optionlist.add(tmpOption)
//        }
//    }
}