package com.example.quizbanktest.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.SingleQuiz
import com.example.quizbanktest.adapters.quiz.LinearLayoutWrapper
import com.example.quizbanktest.adapters.quiz.QuestionAdapter
import com.example.quizbanktest.adapters.quiz.QuestionAddInAddQuiz
import com.example.quizbanktest.adapters.quiz.SPQuizAdapter
import com.example.quizbanktest.databinding.FragmentQuizAddBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.network.quizService
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class QuizAdd : Fragment() {
    private lateinit var binding: FragmentQuizAddBinding
    private var questionList : ArrayList<Question> = ArrayList()
    private var returnToQuizList: ReturnToQuizList? = null
    val addedQList = ArrayList<quizService.QuestionInPostQuiz>()
    private val quizType = Constants.quizTypeSingle
    private var casualDuringTime = ArrayList<Int>()
    private var quizStartDatetime = ""
    private var quizEndDatetime = ""
    private var quizMembers = ArrayList<String>()
    private var quizStatus = Constants.quizStatusDraft
    private lateinit var adapter: QuestionAddInAddQuiz
    interface ReturnToQuizList{
        fun backToQuiz(postQuiz: Quiz?, isConfirm: Boolean)
    }
    fun setReturnToQuizList(returnToQuizList: ReturnToQuizList){
        this.returnToQuizList = returnToQuizList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQuizAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.QuestionList.layoutManager = LinearLayoutWrapper(requireContext())
        binding.QuestionList.setHasFixedSize(true)
        adapter = QuestionAddInAddQuiz(requireActivity(), questionList)
        adapter.setOnDeleteClickListener(object : QuestionAddInAddQuiz.OnDeleteClickListener{
            override fun onclick(position: Int, holder: QuestionAddInAddQuiz.MyViewHolder) {
                questionList.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        binding.QuestionList.adapter = adapter
        binding.QuestionList.isClickable = true

        binding.QuizStartDate.text = LocalDateTime.now().format(Constants.dateTimeFormat)
        binding.cancelBtn.setOnClickListener {
            if(returnToQuizList==null){
                Toast.makeText(requireContext(), "等待call back設定完成再返回!", Toast.LENGTH_SHORT).show()
            }else{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("是否儲存考試?")
                builder.setPositiveButton("儲存考試") { dialog, which ->
                    createQuiz()
                }
                builder.setNegativeButton("放棄儲存") { dialog, which ->
                    returnToQuizList!!.backToQuiz(null, false)
                }
                builder.show()
            }
        }
        binding.confirmBtn.setOnClickListener {
            createQuiz()
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
                for(question in questionAddedList){
                    val newQ = question.copy(_id = UUID.randomUUID().toString())
                    preparedAddedQ.add(newQ)
                }
                if(preparedAddedQ.size>0){
                    questionList.addAll(preparedAddedQ)
                    adapter.notifyDataSetChanged()
                }
            }
        })
        addQuestionFragment.show(childFragmentManager, "addQuestion")
    }

    private fun createQuiz(){
        val titleText = binding.QuizTitle.text.toString()
        val duringTimeMinStr : String = binding.QuizDuringTimeMin.text.toString()
        val duringTimeSecStr : String = binding.QuizDuringTimeSec.text.toString()
        val duringTimeMin = if(duringTimeMinStr.isEmpty()) 0 else duringTimeMinStr.toInt()
        val duringTimeSec = if(duringTimeSecStr.isEmpty()) 0 else duringTimeSecStr.toInt()
        val duringTime = duringTimeMin*60 + duringTimeSec
        quizMembers.add(Constants.userId)

        if(duringTimeMin>200 || duringTime == 0 || duringTimeSec>59){
            AlertDialog.Builder(requireContext()).setTitle("考試時長設定有誤!").setPositiveButton("我懂", null).show()
            return
        }
        if(determineStatus(duringTime))
        {
            val tmpPostQuiz = quizService.PostQuiz(titleText, Constants.quizTypeSingle, quizStatus, duringTime, ArrayList(),
                quizStartDatetime, quizEndDatetime, quizMembers, addedQList)
            ConstantsQuiz.postQuiz(requireContext(), tmpPostQuiz, onSuccess = { postQuiz ->
                returnToQuizList!!.backToQuiz(postQuiz, true)
            }, onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })
        }
    }
    private fun determineStatus(duringTime: Int): Boolean{ //確認能不能新增 以及考試狀態
        var status = Constants.quizStatusReady
        if(addedQList.isEmpty() || duringTime==0){
            status = Constants.quizStatusDraft
        }
        for((index, question) in questionList.withIndex()){
            if(quizType==Constants.quizTypeCasual && question.questionType==Constants.questionTypeShortAnswer){
                Toast.makeText(requireContext(), "多人考試不能有簡答題!", Toast.LENGTH_LONG).show()
                return false
            }
            if(question.questionType==null || returnToQuizList==null){
                Toast.makeText(requireContext(), "出現非常規題目/call back未設定 無法新增", Toast.LENGTH_LONG).show()
                return false
            }
            if(question.answerOptions.isNullOrEmpty() || question.options.isNullOrEmpty() || question.description.isNullOrEmpty()){
                status = Constants.quizStatusDraft
            }
            if(question.createdDate==null){
                question.createdDate = LocalDate.now().format(Constants.dateFormat)
            }
            val tmpAddedQ = quizService.QuestionInPostQuiz(question.title, index.toString(), question.description, question.options,
                question.questionType!!, question.bankType, question.questionBank, question.answerOptions, question.answerDescription,
                question.originateFrom, question.createdDate!!, question.answerImage, question.questionImage, question.tag)
            addedQList.add(tmpAddedQ)
        }

        quizStatus = status
        return true
    }
}