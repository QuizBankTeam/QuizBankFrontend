package com.example.quizbanktest.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.*
import com.example.quizbanktest.databinding.FragmentQuestionAddDialogBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.utils.Constants
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.example.quizbanktest.utils.ConstantsQuiz
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuestionAddDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentQuestionAddDialogBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var F_GroupQuestionBank: AddInGroupQuestionBank? = null
    private var F_SingleQuestionBank: AddInSingleQuestionBank? = null
    private var F_MultiQuiz: AddInQuiz? = null
    private var F_SingleQuiz: AddInQuiz? = null
    private var F_Manually: AddInManually? = null
    private var sendAddedQuiz: SendAddedQuiz? = null
    private val cancelBtnStr = "❌"
    private val backBtnStr = "⬅"
    private var formerView = ArrayList<String>()
    private var formerSourceStr = ArrayList<String>()
    public lateinit var rootContext: Context
    public lateinit var rootActivity: Activity
    private val questionAddedList = ArrayList<Question>()
    private val baseView = "Baseview"
    private var onBaseView = true
    interface SendAddedQuiz{
        fun sendQuiz(questionAddedList: ArrayList<Question>)
    }
    fun setSendAddedQuiz(sendAddedQuiz: SendAddedQuiz){
        this.sendAddedQuiz = sendAddedQuiz
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onStart() {
        super.onStart()
        setDialogHeight()
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity == null) return super.onCreateDialog(savedInstanceState)
        binding = FragmentQuestionAddDialogBinding.inflate(layoutInflater, null, false)
        bottomSheetDialog = BottomSheetDialog(activity!!,  0)
        bottomSheetDialog.setContentView(binding.root)
        rootContext = requireContext()
        rootActivity = requireActivity()
        initView()
        return bottomSheetDialog
    }

    private fun initView(){

        binding.cancelBtn.setOnClickListener {
            backLogic()
        }
        binding.confirmAddition.setOnClickListener {
            Log.d("length of questionAddedList is", questionAddedList.size.toString())
            if(this.sendAddedQuiz!=null){
                sendAddedQuiz!!.sendQuiz(this.questionAddedList)
            }
            dismiss()
        }
        binding.addQuestionGroupQuestionBank.setOnClickListener {
            replaceFragment(AddInGroupQuestionBank::class.java.simpleName)
        }
        binding.addQuestionSingleQuestionBank.setOnClickListener {
            replaceFragment(AddInSingleQuestionBank::class.java.simpleName)
            F_SingleQuestionBank!!.setUpList(rootContext, rootActivity, questionAddedList, ::replaceFragmentFinalLayer)
        }
        binding.addQuestionMultiQuiz.setOnClickListener {
            replaceFragment(Constants.quizTypeCasual)
            F_MultiQuiz!!.setUpList(rootContext, rootActivity, questionAddedList,
                Constants.quizTypeCasual, ::replaceFragmentFinalLayer)
        }
        binding.addQuestionSingleQuiz.setOnClickListener {
            replaceFragment(Constants.quizTypeSingle)
            F_SingleQuiz!!.setUpList(rootContext, rootActivity, questionAddedList,
                Constants.quizTypeSingle, ::replaceFragmentFinalLayer)
        }
        binding.addQuestionManually.setOnClickListener {

        }

    }


    private fun replaceFragment(fragmentName: String){
        binding.cancelBtn.text = backBtnStr
        binding.baseView.visibility = View.GONE
        binding.resourceFrom.visibility = View.VISIBLE
        formerView.clear()
        for(fgIndex in childFragmentManager.fragments.indices){
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(childFragmentManager.fragments[fgIndex])
            transaction.commit()
        }

        val transaction = childFragmentManager.beginTransaction()
        when(fragmentName){
            AddInGroupQuestionBank::class.java.simpleName -> {
                formerView.add(AddInGroupQuestionBank::class.java.simpleName)
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.GroupBank))
                F_GroupQuestionBank = if (F_GroupQuestionBank==null) AddInGroupQuestionBank() else F_GroupQuestionBank
                if(F_GroupQuestionBank!!.isAdded){
                    transaction.show(F_GroupQuestionBank!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_GroupQuestionBank!!).show(F_GroupQuestionBank!!)
                }
            }
            AddInSingleQuestionBank::class.java.simpleName -> {
                formerView.add(AddInSingleQuestionBank::class.java.simpleName)
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.SingleBank))
                F_SingleQuestionBank = if (F_SingleQuestionBank==null) AddInSingleQuestionBank() else F_SingleQuestionBank
                if(F_SingleQuestionBank!!.isAdded){
                    transaction.show(F_SingleQuestionBank!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_SingleQuestionBank!!).show(F_SingleQuestionBank!!)
                }
            }
            Constants.quizTypeCasual -> {
                formerView.add(Constants.quizTypeCasual)
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.CasualQuiz))
                F_MultiQuiz = if (F_MultiQuiz==null) AddInQuiz() else F_MultiQuiz
                if(F_MultiQuiz!!.isAdded){
                    transaction.show(F_MultiQuiz!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_MultiQuiz!!).show(F_MultiQuiz!!)
                }
            }
            Constants.quizTypeSingle -> {
                formerView.add(Constants.quizTypeSingle)
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.SingleQuiz))
                F_SingleQuiz = if (F_SingleQuiz==null) AddInQuiz() else F_SingleQuiz
                if(F_SingleQuiz!!.isAdded){
                    transaction.show(F_SingleQuiz!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_SingleQuiz!!).show(F_SingleQuiz!!)
                }
            }
        }
        transaction.commit()
        formerSourceStr.add(binding.resourceFrom.text.toString())
    }
    private fun replaceFragmentFinalLayer(fragmentName: String, fragment: ChooseQuestion, newResourceName: String){
        val originResource = binding.resourceFrom.text
        binding.resourceFrom.text = String.format(rootContext.getString(R.string.Concatenate2word), originResource, newResourceName)
        formerSourceStr.add(newResourceName)
        for(fgIndex in childFragmentManager.fragments.indices){
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(childFragmentManager.fragments[fgIndex])
            transaction.commit()
        }

        val transaction = childFragmentManager.beginTransaction()
        when(fragmentName){
            AddInGroupQuestionBank::class.java.simpleName -> {
                formerView.add(AddInGroupQuestionBank::class.java.simpleName)
            }
            AddInSingleQuestionBank::class.java.simpleName -> {
                formerView.add(AddInSingleQuestionBank::class.java.simpleName)
                if(fragment.isAdded){
                    transaction.show(fragment)
                }else{
                    transaction.add(R.id.fragment_layer_view, fragment).show(fragment)
                }
            }
            Constants.quizTypeCasual -> {
                formerView.add(Constants.quizTypeCasual)
                if(fragment.isAdded){
                    transaction.show(fragment)
                }else{
                    transaction.add(R.id.fragment_layer_view, fragment).show(fragment)
                }
            }
            Constants.quizTypeSingle -> {
                formerView.add(Constants.quizTypeSingle)
                if(fragment.isAdded){
                    transaction.show(fragment)
                }else{
                    transaction.add(R.id.fragment_layer_view, fragment).show(fragment)
                }
            }
        }
        transaction.commit()
    }
    private fun backLogic(){
        for(fgIndex in childFragmentManager.fragments.indices){
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(childFragmentManager.fragments[fgIndex])
            transaction.commit()
        }
        if(formerView.size==0){
            dismiss()
        }else if(formerView.size==1){
            binding.cancelBtn.text = cancelBtnStr
            binding.baseView.visibility = View.VISIBLE
            binding.resourceFrom.visibility = View.GONE
            formerView.removeLast()
        }else{
            val transaction = childFragmentManager.beginTransaction()
            when(formerView.last()){
                AddInGroupQuestionBank::class.java.simpleName -> {
                    F_GroupQuestionBank?.let { transaction.show(it) }
                }
                AddInSingleQuestionBank::class.java.simpleName -> {
                    F_SingleQuestionBank?.let { transaction.show(it) }
                }
                Constants.quizTypeCasual -> {
                    F_MultiQuiz?.let { transaction.show(it) }
                }
                Constants.quizTypeSingle -> {
                    F_SingleQuiz?.let { transaction.show(it) }
                }
            }
            transaction.commit()
            formerView.removeLast()
        }
        formerSourceStr.removeLastOrNull()
        var newSourceName = if(formerSourceStr.size==0) "" else formerSourceStr[0]
        for(sourceStr in 1 until formerSourceStr.size){
            newSourceName = newSourceName + " / " + formerSourceStr[sourceStr]
        }
        binding.resourceFrom.text =  newSourceName
    }

    class AddInGroupQuestionBank(): Fragment(){
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.list_question_add_second_layer,container,false)
        }
    }

    class AddInSingleQuestionBank(): Fragment(){
        private lateinit var list: androidx.recyclerview.widget.RecyclerView
        private lateinit var adapter: QuestionAddSBAdapter
        private var F_ChooseQuestion = mutableMapOf<Int, ChooseQuestion>()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            return view
        }

        fun setUpList(rootContext: Context, rootActivity: Activity, questionAddedList: ArrayList<Question>,
                      rpFragmentFinalLayer: (String, ChooseQuestion, String) -> (Unit)){
            ConstantsQuestionBankFunction.getAllUserQuestionBanks(rootContext, onSuccess = { bankList->
                list.layoutManager = LinearLayoutWrapper(rootContext)
                list.setHasFixedSize(true)
                adapter = QuestionAddSBAdapter(rootActivity, bankList)
                list.adapter = adapter
                adapter.setSelectClickListener(object : QuestionAddSBAdapter.SelectOnClickListener{
                    override fun onclick(position: Int, holder: QuestionAddSBAdapter.MyViewHolder) {
                        val currentBank = bankList[position]
                        if(F_ChooseQuestion[position]==null){
                            F_ChooseQuestion[position] = ChooseQuestion()
                        }
                        rpFragmentFinalLayer(AddInSingleQuestionBank::class.java.simpleName, F_ChooseQuestion[position]!!, bankList[position].title)
                        ConstantsQuestionFunction.getQuestion(rootContext, currentBank._id, onSuccess = { questionModelList->
                            val tmpQList = ArrayList<Question>()
                            for(qmodel in questionModelList){
                                val tmpQuestion = Question(qmodel._id, qmodel.title, qmodel.number, qmodel.description, qmodel.options, qmodel.questionType, qmodel.bankType, qmodel.questionBank, qmodel.answerOptions, qmodel.answerDescription, Constants.userId, qmodel.originateFrom, qmodel.answerImage, qmodel.questionImage, qmodel.tag, qmodel.createdDate)
                                tmpQList.add(tmpQuestion)
                            }
                            F_ChooseQuestion[position]!!.setUpQuestionList(rootContext, rootActivity, tmpQList, questionAddedList)

                        }, onFailure = {questionWrongTip->
                            Toast.makeText(rootContext, questionWrongTip, Toast.LENGTH_SHORT).show()
                        })
                    }
                })
            }, onFailure = {
                Toast.makeText(rootContext, it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    class AddInQuiz(): Fragment(){
        private lateinit var list: androidx.recyclerview.widget.RecyclerView
        private lateinit var adapter: QuestionAddQuizAdapter
        private var F_ChooseQuestion = mutableMapOf<Int, ChooseQuestion>()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            return view
        }
        fun setUpList(rootContext: Context, rootActivity: Activity, questionAddedList: ArrayList<Question>, quizType: String,
                      rpFragmentFinalLayer: (String, ChooseQuestion, String) -> (Unit)){
              ConstantsQuiz.getAllQuizsWithBatch(rootContext, quizType, batch = 0, onSuccess = { quizList ->
                  if(quizList!=null) {
                      list.layoutManager = LinearLayoutWrapper(requireContext())
                      list.setHasFixedSize(true)
                      adapter = QuestionAddQuizAdapter(requireActivity(), quizList, quizType)
                      list.adapter = adapter
                      list.isClickable = true

                      adapter.setOnClickListener(object :QuestionAddQuizAdapter.SelectOnClickListener{
                          override fun onclick(position: Int, holder: QuestionAddQuizAdapter.MyViewHolder) {
                              val currentQuizTitle = if(quizList!![position].title.isNullOrEmpty()) rootContext.getString(R.string.untitled_EN) else quizList[position].title
                              if(F_ChooseQuestion[position]==null){
                                  F_ChooseQuestion[position] =  ChooseQuestion()
                              }
                              quizList[position].questions = if(quizList[position].questions.isNullOrEmpty()) ArrayList() else quizList[position].questions
                              rpFragmentFinalLayer(quizType, F_ChooseQuestion[position]!!, currentQuizTitle!!)
                              F_ChooseQuestion[position]!!.setOnInitializedListener(object : ChooseQuestion.OnInitializedListener{
                                  override fun onInitialized() {
                                      F_ChooseQuestion[position]!!.setUpQuestionList(rootContext, rootActivity, quizList[position].questions!!, questionAddedList)
                                  }
                              })
                          }
                      })
                  }
              }, onFailure = {
                  Toast.makeText(context, it, Toast.LENGTH_LONG).show()
              })
        }
    }

    class AddInManually(): Fragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }
    class ChooseQuestion(): Fragment(){
        private lateinit var list: androidx.recyclerview.widget.RecyclerView
        private lateinit var adapter: QuestionAddChooseQuestion
        private var initiListener: OnInitializedListener? = null
        private val positionIsSelected = ArrayList<Boolean>()
        interface OnInitializedListener {
            fun onInitialized()
        }
        fun setOnInitializedListener(listener: OnInitializedListener) {
            initiListener = listener
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            if(initiListener!=null){
                initiListener!!.onInitialized()
            }
            return view
        }
        fun setUpQuestionList(rootContext: Context, rootActivity: Activity, questionList: ArrayList<Question>,questionAddedList: ArrayList<Question>){
            if(positionIsSelected.size==0){
                for(index in 0 until questionList.size){
                    positionIsSelected.add(false)
                }
            }
            list.layoutManager = LinearLayoutManager(rootContext)
            list.setHasFixedSize(true)
            adapter = QuestionAddChooseQuestion(rootActivity, questionList, positionIsSelected)
            list.adapter = adapter
            adapter.setSelectClickListener(object : QuestionAddChooseQuestion.SelectOnClickListener {
                override fun onclick(position: Int, holder: QuestionAddChooseQuestion.MyViewHolder) {
                    val selectQ = questionList[position]
                    if (holder.checkBox.isChecked) {
                        questionAddedList.add(selectQ)
                    } else {
                        questionAddedList.remove(selectQ)
                    }
                }
            })
        }
    }
    private fun setDialogHeight(){
        //拿到系统的 bottom_sheet
        val view: FrameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        //获取behavior
        val behavior = BottomSheetBehavior.from(view)
        //设置弹出高度
        behavior.skipCollapsed = true
        behavior.peekHeight = 1000
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    private fun reserveFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction() //开启一个事务
        transaction.replace(R.id.fragment_layer_view, fragment)  //替换容器内的fragment
        transaction.addToBackStack(null)    //返回栈,实现按下back键返回上一个fragment
        transaction.commit()    //提交事务
    }
}