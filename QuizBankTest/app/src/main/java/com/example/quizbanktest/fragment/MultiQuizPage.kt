package com.example.quizbanktest.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.adapters.quiz.MPQuizAdapter
import com.example.quizbanktest.databinding.ListMpQuizBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import java.io.ByteArrayOutputStream
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.LinearLayoutWrapper
import com.example.quizbanktest.adapters.quiz.SPQuizAdapter
import com.example.quizbanktest.utils.ConstantsQuiz
import java.lang.ref.WeakReference

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER//****
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MultiQuizPage : Fragment() {
    companion object {
        var quizListImages =  ArrayList< ArrayList< ArrayList<WeakReference<String>> > >()
    }
    private lateinit var quizBinding: ListMpQuizBinding
    private var QuizList : ArrayList<Quiz> = ArrayList()
    private lateinit var quizListAdapter: MPQuizAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        quizBinding = ListMpQuizBinding.inflate(inflater, container, false)
        return quizBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val quizType = "casual"
        val batch = 0
        val imageBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.society98_1 )
        val base64Image1 = bitmapToString(imageBitmap1)

        ConstantsQuiz.getAllQuizsWithBatch(requireContext(), quizType, batch, onSuccess = { quizList ->
            QuizList = quizList
            for(quiz in quizList){
                val imageArr2 = ArrayList< ArrayList<WeakReference<String>>>()
                for(question in quiz.questions!!){
                    val imageArr1 = ArrayList<WeakReference<String>>()
                    for(image in question.questionImage!!){
                        imageArr1.add(WeakReference(base64Image1))
                    }
                    imageArr2.add(imageArr1)
                }
                SingleQuizPage.Companion.quizListImages.add(imageArr2)
            }

            quizBinding.QuizList.layoutManager = LinearLayoutWrapper(requireContext())
            quizBinding.QuizList.setHasFixedSize(true)
            quizListAdapter = MPQuizAdapter(requireActivity(), QuizList)
            quizBinding.QuizList.adapter = quizListAdapter
            quizBinding.QuizList.isClickable = true
        }, onFailure = {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            initWithoutNetwork()
        })
    }

    fun postQuiz(quiz: Quiz){
        QuizList.add(0, quiz)
        quizListAdapter.notifyDataSetChanged()
    }

    fun putQuiz(quizIndex: Int, tmpQuestions: ArrayList<Question>?, tmpTitle: String?,  tmpCasualDuringTime: ArrayList<Int>?, tmpMembers: ArrayList<String>?, tmpStatus: String, tmpStartDateTime: String?, tmpEndDateTime: String?){
        QuizList[quizIndex].questions = tmpQuestions
        QuizList[quizIndex].title = tmpTitle
        QuizList[quizIndex].casualDuringTime = tmpCasualDuringTime
        QuizList[quizIndex].members = tmpMembers
        QuizList[quizIndex].status = tmpStatus
        QuizList[quizIndex].startDateTime = tmpStartDateTime
        QuizList[quizIndex].endDateTime = tmpEndDateTime
        quizListAdapter.notifyItemChanged(quizIndex)
    }
    fun deleteQuiz(position: Int){
        QuizList.removeAt(position)
        SingleQuizPage.quizListImages.removeAt(position)
        quizListAdapter.notifyDataSetChanged()
//        quizListAdapter.notifyItemChanged(position)
//        for(index in position until QuizList.size){
//            quizListAdapter.notifyItemChanged(index)
//        }
    }
    private fun bitmapToString(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun initWithoutNetwork(){
        val title = "第一次考試"
        val imageBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.society98_1 )
        val imageBitmap2 = BitmapFactory.decodeResource(resources, R.drawable.society9802 )
        val base64Image1 = bitmapToString(imageBitmap1)
        val base64Image2 = bitmapToString(imageBitmap2)
        val ImageArr1 = arrayOf(base64Image1!!).toCollection(ArrayList<String>())
        val ImageArr2 = arrayOf(base64Image2!!).toCollection(ArrayList<String>())
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
        val QuizMember3 = arrayOf("蠟筆小新").toCollection(ArrayList())
        val casualDuring = intArrayOf(20,20).toCollection(ArrayList())
        val casualDuring2 = intArrayOf(20, 20, 30, 30).toCollection(ArrayList())
        val optionText3 = arrayOf("true", "false").toCollection(ArrayList())
        val optionAns3 = arrayOf("true").toCollection(ArrayList())

        var tmpQuestion = Question("123", "題目1", "1", " 圖二為日本統治期間臺灣發電設施之設備容量\n" +
                "變化圖。請問，使 1930 年代設備容量急遽增加\n" + "的設施為何？", optionText,
            "MultipleChoiceS", "", "bank one", optionAns, "an answer description1", "jacky", "jacky",
            ImageArr1, ImageArr1, tag, "2023/05/17")

        var tmpQuestion2 = Question("123", "題目2", "2", "簡介22", optionText2,
            "MultipleChoiceM", "", "bank two", optionAns2, "an answer description2", "jacky","jacky",
            ImageArr2, ImageArr2, tag2, "2023/05/15")

        var tmpQuestion3 = Question("123", "題目2", "3", "簡介2asdaffffffffffff2", optionText3,
            "TrueOrFalse", "", "bank two", optionAns3, "an answer description2", "jacky","jacky",
            ImageArr1, ImageArr1, tag2, "2023/05/15")
        var tmpQuestion4 = Question("123", "題目2", "3", "簡介2asdaffffffffffff2", null,
            "ShortAnswer", "", "bank two", null, "an answer description2", "jacky","jacky",
            ImageArr2, ImageArr2, tag2, "2023/05/15")

        QuestionList.add(tmpQuestion)
        QuestionList2.add(tmpQuestion)
        QuestionList2.add(tmpQuestion2)
        QuestionList.add(tmpQuestion2)
        QuestionList2.add(tmpQuestion3)
        QuestionList2.add(tmpQuestion4)
        val tmpQuiz = Quiz("testmp_quiz1", title, "casual", "ready", 0, casualDuring, "2023-01-05","not yet", QuizMember,QuestionList)
        val tmpQuiz2 = Quiz("testmp_quiz2", "期中考", "casual", "script", 0, casualDuring2, "2023-06-14","not yet", QuizMember2,QuestionList2)

        QuizList.add(tmpQuiz)
        QuizList.add(tmpQuiz2)
    }
}