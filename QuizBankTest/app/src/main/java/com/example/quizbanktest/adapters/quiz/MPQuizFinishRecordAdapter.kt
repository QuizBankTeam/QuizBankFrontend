package com.example.quizbanktest.adapters.quiz

import android.annotation.SuppressLint
import  android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.quizbanktest.R
import com.example.quizbanktest.activity.quiz.MPStartQuiz
import com.example.quizbanktest.models.Option
import com.example.quizbanktest.models.Question
import com.example.quizbanktest.utils.Constants
import com.qdot.mathrendererlib.MathRenderView
import org.w3c.dom.Text

class MPQuizFinishRecordAdapter(private val context: Activity, private val questionList: ArrayList<Question>, private val correctList:ArrayList<Int>,
                                private val totalMembers: Int):
    RecyclerView.Adapter<MPQuizFinishRecordAdapter.MyViewHolder>()  {
    private var onAddListener: SelectOnAddListener? = null
    fun setOnAddListener(onAddListener: SelectOnAddListener) {
        this.onAddListener = onAddListener
    }
    interface SelectOnAddListener {
        fun onAdd(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_mp_total_record, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val q = questionList[position]
        holder.questionType.text = if(q.questionType=="MultipleChoiceS") context.getString(R.string.MultipleChoiceS_CN)
        else if(q.questionType=="MultipleChoiceM") context.getString(R.string.MultipleChoiceM_CN)
        else context.getString(R.string.TrueOrFalse_CN)

        holder.correctRate.text = "正確率: " + (correctList[position]*100/totalMembers).toString() + "%"
        holder.questionNumber.text = (position+1).toString()
        holder.description.text = q.description

        if(MPStartQuiz.Companion.questionImageArr[position].size>0){
            val imgSize = MPStartQuiz.Companion.questionImageArr[position].size
            val imageAdapter = ImageVPAdapter(context, MPStartQuiz.Companion.questionImageArr[position])
            holder.imageViewPager.adapter = imageAdapter
            holder.imageNumber.text = "1 / $imgSize"
            holder.imageViewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    @SuppressLint("SetTextI18n")
                    override fun onPageSelected(imgPosition: Int) {
                        holder.imageNumber.text = "${imgPosition + 1} / $imgSize"
                    }
                }
            )
        }else{
            holder.imageContainer.visibility = View.GONE
        }
        if(q.questionType == Constants.questionTypeTrueOrFalse){
            holder.option.visibility = View.GONE
            holder.trueOrFalseContainer.visibility = View.VISIBLE
            if(q.answerOptions?.get(0) == Constants.TrueOrFalseAnsTrue){
                holder.optionTrue.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_correct))
            }else{
                holder.optionFalse.setBackgroundColor(ContextCompat.getColor(context, R.color.answer_correct))
            }
        }else{
            val optionList = ArrayList<Option>()
            val answerOptions = ArrayList<Int>()
            for(i in q.options?.indices!!) {
                if(i==Constants.optionNum.size-1) break
                val tmpOption = Option(Constants.optionNum[i], q.options!![i])
                optionList.add(tmpOption)

                if (q.answerOptions?.isNotEmpty() == true) {  //找到option中對的選項
                    if(q.answerOptions!!.contains(q.options!![i])){
                        answerOptions.add(i)
                    }
                }
            }
            val optionAdapter = MPQuizFinishOptionAdapter(context, optionList, answerOptions)
            holder.option.layoutManager = LinearLayoutManager(context)
            holder.option.adapter = optionAdapter
            holder.option.setHasFixedSize(true)
        }
        holder.addQBtn.setOnClickListener {
            if(onAddListener!=null){
                onAddListener!!.onAdd(position)
            }
        }

    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val questionNumber: TextView = itemView.findViewById(R.id.question_number) //
        val questionType: TextView = itemView.findViewById(R.id.question_type) //
        val correctRate: TextView = itemView.findViewById(R.id.correct_rate) //
        val imageNumber: TextView = itemView.findViewById(R.id.image_number) //
        val imageViewPager: ViewPager = itemView.findViewById(R.id.image_viewPager) //
        val imageContainer: RelativeLayout = itemView.findViewById(R.id.image_container) //
        val addQBtn: ImageView = itemView.findViewById(R.id.add_question)
        val trueOrFalseContainer: LinearLayout = itemView.findViewById(R.id.TrueOrFalse_container) //
        val optionTrue: TextView = itemView.findViewById(R.id.option_true) //
        val optionFalse: TextView = itemView.findViewById(R.id.option_false) //
        val description: MathRenderView = itemView.findViewById(R.id.question_description) //
        val option: RecyclerView = itemView.findViewById(R.id.QuestionOption) //
    }
}