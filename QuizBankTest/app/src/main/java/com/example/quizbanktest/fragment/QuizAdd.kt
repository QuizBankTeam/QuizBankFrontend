package com.example.quizbanktest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.SingleQuiz
import com.example.quizbanktest.databinding.FragmentQuizAddBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import java.util.*
import kotlin.collections.ArrayList


class QuizAdd : Fragment() {
    private lateinit var binding: FragmentQuizAddBinding
    private var questionList : ArrayList<Question> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQuizAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancelBtn.setOnClickListener {

        }
        binding.confirmBtn.setOnClickListener {

        }
        binding.questionAdd.setOnClickListener {
            addQuestion()
        }
    }

    private fun addQuestion(){
        val addQuestionFragment = QuestionAddDialog()
        addQuestionFragment.setSendAddedQuiz(object : QuestionAddDialog.SendAddedQuiz {
            override fun sendQuiz(questionAddedList: ArrayList<Question>) {
                val preparedAddedQ = ArrayList<Question>()
                val preparedAddedQImages = ArrayList< ArrayList<String> >()
                for(question in questionAddedList){
                    val newQ = question.copy(_id = UUID.randomUUID().toString())
                    preparedAddedQ.add(newQ)
                }
                if(preparedAddedQ.size>0){
                    questionList.addAll(preparedAddedQ)
                }
            }
        })
        addQuestionFragment.show(childFragmentManager, "addQuestion")
    }
}