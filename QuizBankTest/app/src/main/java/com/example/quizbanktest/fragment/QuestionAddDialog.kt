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
import com.example.quizbanktest.R
import com.example.quizbanktest.adapters.quiz.LinearLayoutWrapper
import com.example.quizbanktest.adapters.quiz.QuestionAddChooseQuestion
import com.example.quizbanktest.adapters.quiz.QuestionAddSBAdapter
import com.example.quizbanktest.databinding.FragmentQuestionAddDialogBinding
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.logging.Handler

class QuestionAddDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentQuestionAddDialogBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var F_GroupQuestionBank: AddInGroupQuestionBank? = null
    private var F_SingleQuestionBank: AddInSingleQuestionBank? = null
    private var F_MultiQuiz: AddInMultiQuiz? = null
    private var F_SingleQuiz: AddInSingleQuiz? = null
    private var F_Manually: AddInManually? = null
    private var sendAddedQuiz: SendAddedQuiz? = null
    private val cancelBtnStr = "❌"
    private val backBtnStr = "⬅"
    public lateinit var rootContext: Context
    public lateinit var rootActivity: Activity
    private val questionAddedList = ArrayList<Question>()
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
        Log.d("in on create dialog", "$context")
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
            replaceFragment(AddInMultiQuiz::class.java.simpleName)
        }
        binding.addQuestionSingleQuiz.setOnClickListener {
            replaceFragment(AddInSingleQuiz::class.java.simpleName)
        }
        binding.addQuestionManually.setOnClickListener {

        }

    }


    private fun replaceFragment(fragmentName: String){
        binding.cancelBtn.text = backBtnStr
        binding.baseView.visibility = View.GONE
        binding.resourceFrom.visibility = View.VISIBLE
        onBaseView = false
        for(fgIndex in childFragmentManager.fragments.indices){
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(childFragmentManager.fragments[fgIndex])
            transaction.commit()
        }

        val transaction = childFragmentManager.beginTransaction()
        when(fragmentName){
            AddInGroupQuestionBank::class.java.simpleName -> {
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.GroupBank))
                F_GroupQuestionBank = if (F_GroupQuestionBank==null) AddInGroupQuestionBank() else F_GroupQuestionBank
                if(F_GroupQuestionBank!!.isAdded){
                    transaction.show(F_GroupQuestionBank!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_GroupQuestionBank!!).show(F_GroupQuestionBank!!)
                }
            }
            AddInSingleQuestionBank::class.java.simpleName -> {
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.SingleBank))
                F_SingleQuestionBank = if (F_SingleQuestionBank==null) AddInSingleQuestionBank() else F_SingleQuestionBank
                if(F_SingleQuestionBank!!.isAdded){
                    Log.d("single Question Bank", "is added!")
                    transaction.show(F_SingleQuestionBank!!)
                }else{
                    Log.d("single Question Bank", "is not added!")
                    transaction.add(R.id.fragment_layer_view, F_SingleQuestionBank!!).show(F_SingleQuestionBank!!)
                }
            }
            AddInMultiQuiz::class.java.simpleName -> {
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.CasualQuiz))
                F_MultiQuiz = if (F_MultiQuiz==null) AddInMultiQuiz() else F_MultiQuiz
                if(F_MultiQuiz!!.isAdded){
                    transaction.show(F_MultiQuiz!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_MultiQuiz!!).show(F_MultiQuiz!!)
                }
            }
            AddInSingleQuiz::class.java.simpleName -> {
                binding.resourceFrom.text = String.format(rootContext.getString(R.string.AddQuestion_from_source), rootContext.getString(R.string.SingleQuiz))
                F_SingleQuiz = if (F_SingleQuiz==null) AddInSingleQuiz() else F_SingleQuiz
                if(F_SingleQuiz!!.isAdded){
                    transaction.show(F_SingleQuiz!!)
                }else{
                    transaction.add(R.id.fragment_layer_view, F_SingleQuiz!!).show(F_SingleQuiz!!)
                }
            }
        }
        transaction.commit()
    }
    private fun replaceFragmentFinalLayer(fragmentName: String, fragment: ChooseQuestion, newResourceName: String){
        val originResource = binding.resourceFrom.text
        binding.resourceFrom.text = String.format(rootContext.getString(R.string.Concatenate2word), originResource, newResourceName)
        for(fgIndex in childFragmentManager.fragments.indices){
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(childFragmentManager.fragments[fgIndex])
            transaction.commit()
        }

        val transaction = childFragmentManager.beginTransaction()
        when(fragmentName){
            AddInGroupQuestionBank::class.java.simpleName -> {

            }
            AddInSingleQuestionBank::class.java.simpleName -> {
                if(fragment.isAdded){
                    Log.d("single Question Bank", "is added!")
                    transaction.show(fragment)
                }else{
                    Log.d("single Question Bank", "is NOT added!")
                    transaction.add(R.id.fragment_layer_view, fragment).show(fragment)
                }
            }
            AddInMultiQuiz::class.java.simpleName -> {

            }
            AddInSingleQuiz::class.java.simpleName -> {

            }
        }
        transaction.commit()
    }
    private fun reserveFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction() //开启一个事务
        transaction.replace(R.id.fragment_layer_view, fragment)  //替换容器内的fragment
        transaction.addToBackStack(null)    //返回栈,实现按下back键返回上一个fragment
        transaction.commit()    //提交事务
    }
    private fun backLogic(){

        if(binding.cancelBtn.text == backBtnStr){
            binding.cancelBtn.text = cancelBtnStr
            binding.baseView.visibility = View.VISIBLE
            onBaseView = true
        }else if(binding.cancelBtn.text == cancelBtnStr){
            dismiss()
        }
        if(onBaseView){
            binding.resourceFrom.visibility = View.GONE
            val topFragment = childFragmentManager.fragments.lastOrNull()
            topFragment?.let {
                val transaction = childFragmentManager.beginTransaction()
                transaction.hide(it)
                transaction.commit()
            }
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
    class AddInGroupQuestionBank(): Fragment(){
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.list_question_add_second_layer,container,false)
        }
    }

    class AddInSingleQuestionBank(): Fragment(){
        private lateinit var list: androidx.recyclerview.widget.RecyclerView
        private lateinit var adapter: QuestionAddSBAdapter
        private var F_ChooseQuestion: ChooseQuestion? = null
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            Log.d("context in single question bank is", requireContext().toString())
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
                        F_ChooseQuestion = if(F_ChooseQuestion==null) ChooseQuestion() else F_ChooseQuestion
                        rpFragmentFinalLayer(AddInSingleQuestionBank::class.java.simpleName, F_ChooseQuestion!!, bankList[position].title)
                        ConstantsQuestionFunction.getQuestion(rootContext, currentBank._id, onSuccess = { questionModelList->
                            val tmpQList = ArrayList<Question>()
                            for(qmodel in questionModelList){
                                val tmpQuestion = Question(qmodel._id, qmodel.title, qmodel.number, qmodel.description, qmodel.options, qmodel.questionType, qmodel.bankType, qmodel.questionBank, qmodel.answerOptions, qmodel.answerDescription, "", qmodel.originateFrom, ArrayList(), qmodel.image, qmodel.tag, qmodel.createdDate)
                                tmpQList.add(tmpQuestion)
                            }
                            F_ChooseQuestion!!.setUpQuestionList(rootContext, rootActivity, tmpQList, questionAddedList)
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

    class AddInMultiQuiz(): Fragment(){
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.list_question_add_second_layer,container,false)
        }
    }

    class AddInSingleQuiz(): Fragment(){
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.list_question_add_second_layer,container,false)
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
        private var isInit = false
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            isInit=true
            return view
        }
        fun setUpQuestionList(rootContext: Context, rootActivity: Activity, questionList: ArrayList<Question>,questionAddedList: ArrayList<Question>){

            list.layoutManager = LinearLayoutWrapper(rootContext)
            list.setHasFixedSize(true)
            adapter = QuestionAddChooseQuestion(rootActivity, questionList)
            list.adapter = adapter
            adapter.setSelectClickListener(object : QuestionAddChooseQuestion.SelectOnClickListener {
                override fun onclick(position: Int, holder: QuestionAddChooseQuestion.MyViewHolder) {
                    val selectQ = questionList[position]
                    if (holder.checkBox.isChecked) {
                        questionAddedList.remove(selectQ)
                    } else {
                        questionAddedList.remove(selectQ)
                    }
                    holder.checkBox.isChecked = !holder.checkBox.isChecked
                }
            })
        }
    }

}