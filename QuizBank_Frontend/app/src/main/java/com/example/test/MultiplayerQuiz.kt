package com.example.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.ActivityMainBinding
import com.example.test.databinding.MpQuizBinding
import com.example.test.databinding.SingleQuestionBinding

class MultiplayerQuiz: AppCompatActivity() {
    private lateinit var mpQuizBinding: MpQuizBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mpQuizBinding = MpQuizBinding.inflate(layoutInflater)
        setContentView(mpQuizBinding.root)

        mpQuizBinding.QuizList.layoutManager = LinearLayoutManager(this)
        mpQuizBinding.QuizList.setHasFixedSize(true)

        init()
//      binding.optionsView.adapter = OptionAdapter(this, optionlist)

//      login_button.setOnClickListener{ buttonClick() }
    }

    private fun buttonClick(){
        val tmpQuiz = QuizList[0]
        intent.setClass(this@MultiplayerQuiz, SingleQuiz::class.java)
        intent.putExtra("Key_id", tmpQuiz.id)
        intent.putExtra("Key_title", tmpQuiz.title)
        intent.putExtra("Key_type", tmpQuiz.type)
        intent.putExtra("Key_status", tmpQuiz.status)
        intent.putExtra("Key_duringTime", tmpQuiz.duringTime)
        intent.putExtra("Key_startDate", tmpQuiz.startDate)
        intent.putExtra("Key_endDate", tmpQuiz.endDate)
        intent.putExtra("Key_members", tmpQuiz.members)
        intent.putExtra("Key_questions", tmpQuiz.questions)
        startActivity(intent)

    }

    private fun init() //mp_quiz -> single_quiz -> single_question
    {
        val title: String = "第一次考試"
        val startDate: String = "2023/05/20"
        val endDate: String = "2023/05/21"
        val QuestionList: ArrayList<Question> = ArrayList()
        val optionText = arrayOf("恆春的核能發電廠", "高雄的火力發電廠", "C demo text 789", "D demo text 101112").toCollection(ArrayList())
        val optionAns = arrayOf("A demo text 123").toCollection(ArrayList())
        val optionText2 = arrayOf("A cc text 123", "B ddd text 456", "C gggg text 789", "D asdfasdf text 101112").toCollection(ArrayList())
        val optionAns2 = arrayOf("B ddd text 456").toCollection(ArrayList())
        val tag = arrayOf("98年", "社會", "歷史").toCollection(ArrayList())
        val tag2 = arrayOf("98年", "公民", "地理").toCollection(ArrayList())
        val QuizMember = arrayOf("jacky","wcy").toCollection(ArrayList())
        val casualDuring = intArrayOf(20,20).toCollection(ArrayList())
        var tmpQuestion = Question("123", "題目1", "123", " 圖二為日本統治期間臺灣發電設施之設備容量\n" +
                        "變化圖。請問，使 1930 年代設備容量急遽增加\n" + "的設施為何？", optionText,
                    "single", "bank one", optionAns, "an answer description1", "jacky",
                    R.drawable.society98_1, tag, "2023/05/17")
        QuestionList.add(tmpQuestion)

        tmpQuestion = Question("123", "題目2", "123", "簡介22", optionText2,
                    "multi", "bank two", optionAns2, "an answer description2", "jacky", R.drawable.society9802, tag2, "2023/05/15")
        QuestionList.add(tmpQuestion)

        val tmpQuiz = Quiz("testmp_quiz", title, "casual", "ready", 0, casualDuring, "not yet","not yet", QuizMember,QuestionList)

        QuizList.add(tmpQuiz)

    }
}