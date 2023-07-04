package com.example.test.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.Adapter.SPRecordAdapter
import com.example.test.R
import com.example.test.databinding.MpRecordBinding
import com.example.test.model.Question
import com.example.test.model.Quiz
import com.example.test.model.QuizRecord

class MultiRecordPage: Fragment() {
    private lateinit var recordBinding: MpRecordBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()
    private var recordList: ArrayList<QuizRecord> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recordBinding = MpRecordBinding.inflate(inflater, container, false)
        return recordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        recordBinding.QuizRecordList.layoutManager = LinearLayoutManager(requireContext())
//        recordBinding.QuizRecordList.setHasFixedSize(true)
//        val recordAdapter = SPRecordAdapter(requireActivity(), recordList)
//        recordBinding.QuizRecordList.adapter = recordAdapter
//        recordBinding.QuizRecordList.isClickable = true


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
    private fun init(){
        val title = "第一次考試"
        Log.d("start initing=","")
        val QuestionList: ArrayList<Question> = ArrayList()
        val QuestionList2: ArrayList<Question> = ArrayList()

        val optionText = arrayOf("恆春的核能發電廠", "高雄的火力發電廠", "C demo text 789", "D demo text 101112").toCollection(ArrayList())
        val optionAns = arrayOf("C demo text 789").toCollection(ArrayList())
        val optionText2 = arrayOf("A cc text 123", "B ddd text 456", "C gggg text 789", "D asdfasdf text 101112").toCollection(ArrayList())
        val optionAns2 = arrayOf("B ddd text 456").toCollection(ArrayList())
        val tag = arrayOf("98年", "社會", "歷史").toCollection(ArrayList())
        val tag2 = arrayOf("94年", "地理").toCollection(ArrayList())
        val QuizMember = arrayOf("jl","wcy","yc","wt","cy").toCollection(ArrayList())
        val QuizMember2 = arrayOf("jl", "jacky", "hehe", "jjs").toCollection(ArrayList())


        var tmpQuestion = Question("123", "題目1", "1", " 圖二為日本統治期間臺灣發電設施之設備容量\n" +
                "變化圖。請問，使 1930 年代設備容量急遽增加\n" + "的設施為何？", optionText,
            "MultipleChoiceS", "bank one", optionAns, "an answer description1", "jacky",
            R.drawable.society98_1, tag, "2023/05/17")

        var tmpQuestion2 = Question("123", "題目2", "2", "簡介22", optionText2,
            "MultipleChoiceM", "bank two", optionAns2, "an answer description2", "jacky",
            R.drawable.society9802, tag2, "2023/05/15")

        QuestionList.add(tmpQuestion)
        QuestionList.add(tmpQuestion2)
        QuestionList2.add(tmpQuestion)
        QuestionList2.add(tmpQuestion)
        QuestionList2.add(tmpQuestion2)

        val tmpQuiz = Quiz("sp_quiz1", title, "single", "script", 600, null, "2023-01-05","not yet", null,QuestionList)
        val tmpQuiz2 = Quiz("sp_quiz1", "期中考", "single", "ready", 900, null, "2023-06-14","not yet", null,QuestionList2)

        QuizList.add(tmpQuiz)
        QuizList.add(tmpQuiz2)
    }
    companion object {
    }
}