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
import com.example.quizbanktest.adapters.quiz.OptionAdapter
import com.example.quizbanktest.adapters.quiz.QuestionAddSBAdapter
import com.example.quizbanktest.databinding.FragmentQuestionAddDialogBinding
import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuestionAddDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentQuestionAddDialogBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var F_GroupQuestionBank: AddInGroupQuestionBank? = null
    private var F_SingleQuestionBank: AddInSingleQuestionBank? = null
    private var F_MultiQuiz: AddInMultiQuiz? = null
    private var F_SingleQuiz: AddInSingleQuiz? = null
    private var F_Manually: AddInManually? = null
    private lateinit var singleQuestionBankList: androidx.recyclerview.widget.RecyclerView
    private val cancelBtnStr = "❌"
    private val backBtnStr = "⬅"
    public lateinit var rootContext: Context
    public lateinit var rootActivity: Activity
    private val questionAddedList = ArrayList<QuestionModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if(binding.cancelBtn.text == backBtnStr){
                binding.cancelBtn.text = cancelBtnStr
                binding.baseView.visibility = View.VISIBLE
            }else if(binding.cancelBtn.text == cancelBtnStr){
                dismiss()
            }
        }
        binding.confirmAddition.setOnClickListener {
            dismiss()
        }
        binding.addQuestionGroupQuestionBank.setOnClickListener {
            if(F_GroupQuestionBank==null){
                F_GroupQuestionBank = AddInGroupQuestionBank()
                replaceFragment(F_GroupQuestionBank!!)
            }else{
                replaceFragment(F_GroupQuestionBank!!)
            }
        }
        binding.addQuestionSingleQuestionBank.setOnClickListener {
            val isNull = if(F_SingleQuestionBank==null) 1 else 0
            if(isNull==1){
                F_SingleQuestionBank = AddInSingleQuestionBank()
                replaceFragment(F_SingleQuestionBank!!)
                F_SingleQuestionBank!!.setUpList(rootActivity, rootContext, questionAddedList)
            }
            else{
                replaceFragment(F_SingleQuestionBank!!)
            }
        }
        binding.addQuestionMultiQuiz.setOnClickListener {
            if(F_MultiQuiz==null){
                F_MultiQuiz = AddInMultiQuiz()
                replaceFragment(F_MultiQuiz!!)
            }else{
                replaceFragment(F_MultiQuiz!!)
            }
        }
        binding.addQuestionSingleQuiz.setOnClickListener {
            if(F_SingleQuiz==null){
                F_SingleQuiz = AddInSingleQuiz()
                replaceFragment(F_SingleQuiz!!)
            }else{
                replaceFragment(F_SingleQuiz!!)
            }
        }
        binding.addQuestionManually.setOnClickListener {
            if(F_Manually==null){
                F_Manually = AddInManually()
                replaceFragment(F_Manually!!)
            }else{
                replaceFragment(F_Manually!!)
            }
        }

    }


    private fun replaceFragment(fragment: Fragment){
        binding.cancelBtn.text = backBtnStr
        binding.baseView.visibility = View.GONE
        val transaction = childFragmentManager.beginTransaction() //开启一个事务
        transaction.replace(R.id.fragment_layer_view, fragment)  //替换容器内的fragment
        transaction.addToBackStack(null)    //返回栈,实现按下back键返回上一个fragment
        transaction.commit()    //提交事务
    }
    private fun reserveFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction() //开启一个事务
        transaction.replace(R.id.fragment_layer_view, fragment)  //替换容器内的fragment
        transaction.addToBackStack(null)    //返回栈,实现按下back键返回上一个fragment
        transaction.commit()    //提交事务
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
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view  = inflater.inflate(R.layout.list_question_add_second_layer,container,false)
            list = view.findViewById(R.id.second_layer_list)
            Log.d("now initing bank list recycle view", list.toString())
            return view
        }
        fun setUpList(rootActivity: Activity, rootContext: Context, questionAddedList: ArrayList<QuestionModel>){
            ConstantsQuestionBankFunction.getAllUserQuestionBanks(rootContext, onSuccess = { bankList->
                list.layoutManager = LinearLayoutWrapper(requireContext())
                list.setHasFixedSize(true)
                adapter = QuestionAddSBAdapter(rootActivity, bankList)
                list.adapter = adapter
                adapter.setSelectClickListener(object : QuestionAddSBAdapter.SelectOnClickListener{
                    override fun onclick(position: Int, holder: QuestionAddSBAdapter.MyViewHolder) {
                        val currentBank = bankList[position]
                        Toast.makeText(rootContext, "你加入了第${position}個題庫", Toast.LENGTH_SHORT).show()
//                        ConstantsQuestionFunction.getQuestion(requireContext(), currentBank._id, onSuccess = { questionMoedlList->
//                            questionAddedList.add(questionAddedList[0])
//                            Toast.makeText(rootContext, "你加入了${questionMoedlList[0].title}", Toast.LENGTH_SHORT).show()
//                        }, onFailure = {questionWrongTip->
//                            Toast.makeText(requireContext(), questionWrongTip, Toast.LENGTH_SHORT).show()
//                        })
                    }
                })
            }, onFailure = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.list_question_add_second_layer,container,false)
        }
    }

}