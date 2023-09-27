package com.example.quizbanktest.activity.quiz

import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout



import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizbanktest.activity.BaseActivity
import com.example.quizbanktest.adapters.quiz.QuizPageAdapter
import com.example.quizbanktest.databinding.ActivityQuizPageBinding
import com.example.quizbanktest.fragment.MultiQuizPage
import com.example.quizbanktest.fragment.QuizAdd
import com.example.quizbanktest.fragment.SingleQuizPage
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.models.Quiz
import com.example.quizbanktest.network.quizService
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuiz


class QuizPage: BaseActivity() {
    private lateinit var quizPageBinding: ActivityQuizPageBinding
    private lateinit var fragmentAdapter: QuizPageAdapter
    private lateinit var SPFragment: SingleQuizPage
    private lateinit var MPFragment: MultiQuizPage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizPageBinding = ActivityQuizPageBinding.inflate(layoutInflater)
        setContentView(quizPageBinding.root)

        quizPageBinding.quizAdd.setOnClickListener {
            if(quizPageBinding.quizPager.currentItem==0) {
                Log.d("item is 0", "")
                addQuiz(Constants.quizTypeSingle)
            }else if(quizPageBinding.quizPager.currentItem==1){
                addQuiz(Constants.quizTypeCasual)
                Log.d("item is 1", "")
            }
        }
        quizPageBinding.quizRecord.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RecordPage::class.java)
            startActivity(intent)
        }

        SPFragment = SingleQuizPage()
        MPFragment = MultiQuizPage()

        fragmentAdapter = QuizPageAdapter(supportFragmentManager, lifecycle, SPFragment, MPFragment)
        quizPageBinding.selectQuizMode.addTab(quizPageBinding.selectQuizMode.newTab().setText("單人"))
        quizPageBinding.selectQuizMode.addTab(quizPageBinding.selectQuizMode.newTab().setText("多人"))

        quizPageBinding.quizPager.adapter = fragmentAdapter


        quizPageBinding.selectQuizMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    quizPageBinding.quizPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        quizPageBinding.quizPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                quizPageBinding.selectQuizMode.selectTab(quizPageBinding.selectQuizMode.getTabAt(position))
            }
        })

        setupNavigationView()
        doubleCheckExit()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("back in Quiz Page request code=", requestCode.toString())
        if(resultCode == Constants.RESULT_DELETE){
            if(data != null){
                val quizType = data.getStringExtra("Key_type").toString()
                if(quizType == Constants.quizTypeSingle){
                    fragmentAdapter.getSPFragment().deleteQuiz(requestCode)

                }else if(quizType == Constants.quizTypeCasual){
                    fragmentAdapter.getMPFragment().deleteQuiz(requestCode)
                }
            }
        }
        else if(requestCode<1000){
            if(resultCode==RESULT_OK && data!=null){
                val quizType = data.getStringExtra("Key_type").toString()
                val tmpStatus = data.getStringExtra("Key_status")
                val tmpStartDateTime = data.getStringExtra("Key_startDateTime")
                val tmpEndDateTime = data.getStringExtra("Key_endDateTime")
                val tmpQuestions = data.getParcelableArrayListExtra<Question>("Key_questions")
                val tmpTitle = data.getStringExtra("Key_title")

                if(quizType == Constants.quizTypeSingle){
                    val tmpDuringTime = data.getIntExtra("Key_duringTime", 0)
                    fragmentAdapter.getSPFragment().putQuiz(requestCode, tmpQuestions, tmpTitle, tmpDuringTime, tmpStatus, tmpStartDateTime, tmpEndDateTime)

                }else if(quizType == Constants.quizTypeCasual){
                    val tmpMembers = data.getStringArrayListExtra("Key_members")
                    val tmpCasualDuringTime = data.getIntegerArrayListExtra("Key_casualDuringTime")
                    fragmentAdapter.getMPFragment().putQuiz(requestCode, tmpQuestions, tmpTitle, tmpCasualDuringTime, tmpMembers, tmpStatus!!, tmpStartDateTime, tmpEndDateTime)
                }
            }
        }
    }

    private fun addQuiz(addedQuizType: String){
        val quizAdd = QuizAdd(addedQuizType)
        val transaction = supportFragmentManager.beginTransaction()
        quizAdd.setReturnToQuizList(object : QuizAdd.ReturnToQuizList{
            override fun backToQuiz(postQuiz: Quiz?, isConfirm: Boolean) {
                if(isConfirm){
                    if(addedQuizType == Constants.quizTypeSingle) {
                        fragmentAdapter.getSPFragment().postQuiz(postQuiz!!)
                    }else if(addedQuizType == Constants.quizTypeCasual){
                        fragmentAdapter.getMPFragment().postQuiz(postQuiz!!)
                    }
                }
                val finishTransaction = supportFragmentManager.beginTransaction()
                finishTransaction.remove(quizAdd).commit()
                quizPageBinding.quizPageContainer.visibility = View.VISIBLE
                quizPageBinding.lowerToolList.llActionButtons.visibility = View.VISIBLE
                quizPageBinding.addQuizContainer.visibility = View.GONE
            }
        })
        quizPageBinding.quizPageContainer.visibility = View.GONE
        quizPageBinding.lowerToolList.llActionButtons.visibility = View.GONE
        quizPageBinding.addQuizContainer.visibility = View.VISIBLE
        transaction.add(quizPageBinding.addQuizContainer.id, quizAdd).show(quizAdd)
        transaction.commit()
    }
//    private fun addQuiz(){
//        if(quizPageBinding.quizPager.currentItem==0){
//            val tmpMembers = ArrayList<String>()
//            tmpMembers.add(Constants.userId)
//            val tmpTag = ArrayList<String>()
//            val tmpOptionsM = arrayListOf<String>("test1", "test2", "test3", "test4", "test5")
//            val tmpOptionsS = arrayListOf<String>("test1", "test2", "test3", "test4")
//            val tmpOptionsTf = arrayListOf<String>("true", "false")
//            val tmpOptionsSa = ArrayList<String>()
//            val tmpAnswerOptionsS = arrayListOf<String>("test1")
//            val tmpAnswerOptionsM = arrayListOf<String>("test1", "test2")
//            val tmpAnswerOptionsTf = arrayListOf<String>("true")
//            tmpTag.add("資料結構")
//
//            val qList = ArrayList<quizService.QuestionInPostQuiz>()
//            val tmpPostQuizQuestionS = quizService.QuestionInPostQuiz("單選測試", "1", "test MultipleChoiceS", tmpOptionsS, "MultipleChoiceS", "none", "none", tmpAnswerOptionsS, "this is an answerDescription S", Constants.userId, "2023-6-29", ArrayList(), ArrayList(), tmpTag)
//            val tmpPostQuizQuestionM = quizService.QuestionInPostQuiz("多選測試", "2", "test MultipleChoiceM", tmpOptionsM, "MultipleChoiceM", "none", "none", tmpAnswerOptionsM, "this is an answerDescription M", Constants.userId, "2023-6-29", ArrayList(), ArrayList(), tmpTag)
//            val tmpPostQuizQuestionTF = quizService.QuestionInPostQuiz("是非測試", "3", "test True or False", tmpOptionsTf, "TrueOrFalse", "none", "none", tmpAnswerOptionsTf, "this is an true or false description", Constants.userId, "2023-6-29", ArrayList(), ArrayList(), tmpTag)
//            val tmpPostQuizQuestionSA = quizService.QuestionInPostQuiz("簡答測試", "4", "test short answer", tmpOptionsSa, "ShortAnswer", "none", "none", tmpOptionsSa, "this is an short answer description", Constants.userId, "2023-6-29", ArrayList(), ArrayList(), tmpTag)
//
//            qList.add(tmpPostQuizQuestionS)
//            qList.add(tmpPostQuizQuestionM)
//            qList.add(tmpPostQuizQuestionTF)
//            qList.add(tmpPostQuizQuestionSA)
//            val tmpPostQuiz = quizService.PostQuiz("test quiz",  "single", "ready", 100, ArrayList(), "2023-07-13 13:04:10", "2023-07-13 13:04:10", tmpMembers, qList)
//            ConstantsQuiz.postQuiz(this, tmpPostQuiz, onSuccess = { postQuiz ->
//                if(postQuiz.type == Constants.quizTypeSingle){
//                    fragmentAdapter.getSPFragment().postQuiz(postQuiz)
//                }else if(postQuiz.type == Constants.quizTypeCasual){
//                    fragmentAdapter.getMPFragment().postQuiz(postQuiz)
//                }
//            }, onFailure = {
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            })
//        }
//    }

}