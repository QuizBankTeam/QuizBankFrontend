package com.example.test.Activity

import android.content.Intent
import com.example.test.Adapter.QuizAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.R
import com.example.test.databinding.MpQuizBinding
import com.example.test.model.Question
import com.example.test.model.Quiz

class MultiplayerQuiz: AppCompatActivity() {
    private lateinit var mpQuizBinding: MpQuizBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mpQuizBinding = MpQuizBinding.inflate(layoutInflater)
        setContentView(mpQuizBinding.root)
        init()
        mpQuizBinding.QuizList.layoutManager = LinearLayoutManager(this)
        mpQuizBinding.QuizList.setHasFixedSize(true)
        val quizadapter = QuizAdapter(this, QuizList)
        mpQuizBinding.QuizList.adapter = quizadapter
        mpQuizBinding.QuizList.isClickable = true

        mpQuizBinding.backBtn.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("in ", "multiplayer Quiz")
        Log.d("request code=", requestCode.toString())
        var tmpQuiz = QuizList[requestCode]
        if (data != null) {
            tmpQuiz.questions = data.getParcelableArrayListExtra<Question>("Key_questions") as ArrayList<Question>
            tmpQuiz.title = data.getStringExtra("Key_title").toString()
        }
        QuizList[requestCode] = tmpQuiz
    }


    private fun init() //mp_quiz -> single_quiz -> single_question
    {
        val title: String = "第一次考試"
        val startDate: String = "2023/05/20"
        val endDate: String = "2023/05/21"
        val QuestionList: ArrayList<Question> = ArrayList()
        val optionText = arrayOf("恆春的核能發電廠", "高雄的火力發電廠", "C demo text 789", "D demo text 101112").toCollection(ArrayList())
        val optionAns = arrayOf("C demo text 789").toCollection(ArrayList())
        val optionText2 = arrayOf("A cc text 123", "B ddd text 456", "C gggg text 789", "D asdfasdf text 101112").toCollection(ArrayList())
        val optionAns2 = arrayOf("B ddd text 456").toCollection(ArrayList())
        val tag = arrayOf("98年", "社會", "歷史").toCollection(ArrayList())
        val tag2 = arrayOf("94年", "地理").toCollection(ArrayList())
        val QuizMember = arrayOf("jl","wcy","yc","wt","cy").toCollection(ArrayList())
        val QuizMember2 = arrayOf("jl").toCollection(ArrayList())
        val QuizMember3 = arrayOf("蠟筆小新").toCollection(ArrayList())
        val casualDuring = intArrayOf(20,20).toCollection(ArrayList())
        var tmpQuestion = Question("123", "題目1", "123", " 圖二為日本統治期間臺灣發電設施之設備容量\n" +
                        "變化圖。請問，使 1930 年代設備容量急遽增加\n" + "的設施為何？", optionText,
                    "single", "bank one", optionAns, "an answer description1", "jacky",
            R.drawable.society98_1, tag, "2023/05/17")
        QuestionList.add(tmpQuestion)

        tmpQuestion = Question("123", "題目2", "123", "簡介22", optionText2,
                    "multi", "bank two", optionAns2, "an answer description2", "jacky",
            R.drawable.society9802, tag2, "2023/05/15")
        QuestionList.add(tmpQuestion)

        val tmpQuiz = Quiz("testmp_quiz1", title, "casual", "ready", 0, casualDuring, "2023-01-05","not yet", QuizMember,QuestionList)
        val tmpQuiz2 = Quiz("testmp_quiz2", "期中考", "casual", "script", 0, casualDuring, "2023-06-14","not yet", QuizMember2,QuestionList)

        QuizList.add(tmpQuiz)
        QuizList.add(tmpQuiz2)
      
    }
    private fun buttonClicked()
    {

    }
}