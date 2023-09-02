package com.example.quizbanktest.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.LinearLayoutWrapper
import com.example.quizbanktest.adapters.quiz.SPQuizAdapter
import com.example.quizbanktest.databinding.ListSpQuizBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SingleQuizPage : Fragment() {
    companion object {
        var quizListImages =  ArrayList< ArrayList< ArrayList<String> > >()
    }

    private lateinit var quizBinding: ListSpQuizBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()
    private lateinit var quizListAdapter: SPQuizAdapter
    private var imageArr = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        quizBinding = ListSpQuizBinding.inflate(inflater, container, false)
        return quizBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val quizType = Constants.quizTypeSingle
        val batch = 0
        val imageBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.society98_1 )
        val base64Image1 = Constants.bitmapToString(imageBitmap1)

        ConstantsQuiz.getAllQuizsWithBatch(requireContext(), quizType, batch, onSuccess = { quizList ->
            if(quizList!=null) {
                QuizList = quizList
                for (quiz in quizList) {
                    val imageArr2 = ArrayList<ArrayList<String>>()
                    for (question in quiz.questions!!) {
                        val imageArr1 = ArrayList<String>()
                        for (image in question.questionImage!!) {
                            imageArr1.add(image)
                        }
                        imageArr2.add(imageArr1)
                    }
                    SingleQuizPage.Companion.quizListImages.add(imageArr2)
                }

                quizBinding.QuizList.layoutManager = LinearLayoutWrapper(requireContext())
                quizBinding.QuizList.setHasFixedSize(true)
                quizListAdapter = SPQuizAdapter(requireActivity(), QuizList)
                quizBinding.QuizList.adapter = quizListAdapter
                quizBinding.QuizList.isClickable = true
            }

        }, onFailure = {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            initWithoutNetwork()
        })
    }


    fun postQuiz(quiz: Quiz){
        QuizList.add(0, quiz)//?????? notify的先後順序有差 會影響到app crushed掉 甚至連list顯示都會壞掉
//        quizListAdapter.notifyItemChanged(QuizList.size)
//        for(index in 0 until QuizList.size){
//            quizListAdapter.notifyItemChanged(index)
//        }
        quizListAdapter.notifyDataSetChanged()
        val imageArr2 = ArrayList< ArrayList<String> >()
        for(question in quiz.questions!!){
            val imageArr1 = ArrayList<String>()
            for(image in question.questionImage!!){
                imageArr1.add(image)
            }
            imageArr2.add(imageArr1)
        }
        SingleQuizPage.Companion.quizListImages.add(0, imageArr2)

    }
    fun putQuiz(position: Int, questions: ArrayList<Question>?, title: String?, duringTime: Int, status: String?, startDateTime: String?, endDateTime: String?){
        QuizList[position].title = title
        QuizList[position].questions = questions
        QuizList[position].duringTime = duringTime
        QuizList[position].status = status
        QuizList[position].startDateTime = startDateTime
        QuizList[position].endDateTime = endDateTime
        quizListAdapter.notifyItemChanged(position)
    }

    fun deleteQuiz(position: Int){
        QuizList.removeAt(position)
        quizListImages.removeAt(position)
        quizListAdapter.notifyDataSetChanged()
//        quizListAdapter.notifyItemChanged(position)
//        for(index in position until QuizList.size){
//            quizListAdapter.notifyItemChanged(index)
//        }
    }

    private fun initWithoutNetwork(){
        val title = "第一次考試"
        val imageBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.society98_1 )
        val imageBitmap2 = BitmapFactory.decodeResource(resources, R.drawable.society9802 )
        val base64Image1 = Constants.bitmapToString(imageBitmap1)
        val base64Image2 = Constants.bitmapToString(imageBitmap2)
        val ImageArr1 = arrayOf(base64Image1!!, base64Image2!!).toCollection(ArrayList<String>())
        imageArr = ImageArr1
        val QuestionList: ArrayList<Question> = ArrayList()
        val QuestionList2: ArrayList<Question> = ArrayList()

        val optionText = arrayOf("恆春的核能發電廠", "高雄的火力發電廠", "C demo text 789", "D demo text 101112").toCollection(ArrayList())
        val optionAns = arrayOf("C demo text 789").toCollection(ArrayList())
        val optionText2 = arrayOf("A cc text 123", "B ddd text 456", "C gggg text 789", "D asdfasdf text 101112").toCollection(ArrayList())
        val optionAns2 = arrayOf("B ddd text 456").toCollection(ArrayList())
        val optionText3 = arrayOf("true", "false").toCollection(ArrayList())
        val optionAns3 = arrayOf("true").toCollection(ArrayList())

        val tag = arrayOf("98年", "社會", "歷史").toCollection(ArrayList())
        val tag2 = arrayOf("94年", "地理").toCollection(ArrayList())
        val QuizMember = arrayOf("jl","wcy","yc","wt","cy").toCollection(ArrayList())
        val QuizMember2 = arrayOf("jl", "jacky", "hehe", "jjs").toCollection(ArrayList())


        var tmpQuestion = Question("123", "題目1", "1", " 圖二為日本統治期間臺灣發電設施之設備容量\n" +
                "變化圖。請問，使 1930 年代設備容量急遽增加\n" + "的設施為何？", optionText,
            "MultipleChoiceS", "", "bank one", optionAns, "an answer description1", "jacky","jacky",
            null, null, tag, "2023/05/17")

        var tmpQuestion2 = Question("123", "題目2", "2", "簡介22", optionText2,
            "MultipleChoiceM", "", "bank two", optionAns2, "an answer description2", "jacky","jacky",
            null, null, tag2, "2023/05/15")

        var tmpQuestion3 = Question("123", "題目2", "3", "簡介2asdaffffffffffff2", optionText3,
            "TrueOrFalse", "", "bank two", optionAns3, "an answer description2", "jacky","jacky",
            null, null, tag2, "2023/05/15")
        var tmpQuestion4 = Question("123", "題目2", "3", "簡介2asdaffffffffffff2", null,
            "ShortAnswer", "", "bank two", null, "an answer description2", "jacky","jacky",
            null, null, tag2, "2023/05/15")

        QuestionList.add(tmpQuestion)
        QuestionList.add(tmpQuestion2)
        QuestionList2.add(tmpQuestion)
        QuestionList2.add(tmpQuestion2)
        QuestionList2.add(tmpQuestion3)
        QuestionList2.add(tmpQuestion4)
        val tmpQuiz = Quiz("sp_quiz1", title, "single", "script", 600, null, "2023-01-05","not yet", null,QuestionList)
        val tmpQuiz2 = Quiz("sp_quiz1", "期中考", "single", "ready", 900, null, "2023-06-14","not yet", null,QuestionList2)

        QuizList.add(tmpQuiz)
        QuizList.add(tmpQuiz2)

        quizBinding.QuizList.layoutManager = LinearLayoutManager(requireContext())
        quizBinding.QuizList.setHasFixedSize(true)
        val quizAdapter = SPQuizAdapter(requireActivity(), QuizList)
        quizBinding.QuizList.adapter = quizAdapter
        quizBinding.QuizList.isClickable = true
    }
}