package com.example.quizbanktest.adapters.bank

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel

import com.example.quizbanktest.models.QuestionModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import com.example.quizbanktest.utils.ConstantsQuestionFunction
import java.util.*
import kotlin.collections.ArrayList

class QuestionRecyclerViewAdapter(var context: Context,
                                  var activity: AppCompatActivity,
                                  var questionModels: ArrayList<QuestionModel>,
                                  var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<QuestionRecyclerViewAdapter.MyViewHolder>() {

    /** the arrayList that recyclerView use */
    var arrayList: java.util.ArrayList<QuestionModel> = questionModels

    /** store the original status of arrayList, which means questionBankModels above */
    var arrayListFilter: java.util.ArrayList<QuestionModel> = java.util.ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_questioncard, parent, false)
            arrayListFilter.clear()
            arrayListFilter.addAll(arrayList)
            return MyViewHolder(view, recyclerViewInterface)
        } catch (e: Exception) {
            Log.e("QuestionRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvQuestionTitle.text = questionModels[position].title
        holder.tvQuestionType.text = questionModels[position].questionType
        holder.tvQuestionCreatedDate.text = questionModels[position].createdDate

        holder.tvQuestionType.setBackgroundColor(Color.parseColor("#ffeb3b"))
    }

    override fun getItemCount(): Int { return questionModels.size }

    fun getFilter(): Filter { return mFilter }

    /** use Filter() to filter the words */
    private var mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            /** the arrayList used to copy original arrayList */
            val filteredList: java.util.ArrayList<QuestionModel> = java.util.ArrayList()

            /** if there's no input, use the original arrayList */
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(arrayListFilter)
            } else {
                // if not, filter the title of banks
                for (movie in arrayListFilter) {
                    if (movie.title.lowercase(Locale.getDefault()).contains(
                            constraint.toString().lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        filteredList.add(movie)
                    }
                }
            }
            /** return the result of filter */
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        /** give the result of filter to the recyclerView's arrayList and refresh it */
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            arrayList.clear()
            arrayList.addAll(results.values as Collection<QuestionModel>)
            notifyDataSetChanged()
        }
    }

    fun setItem(position: Int, data: QuestionModel) {
        ConstantsQuestionFunction.putQuestion(activity, data,
            onSuccess = {
                Log.e("QuestionRecyclerViewAdapter", "put successfully")
                questionModels[position] = data
                notifyItemChanged(position)
            },
            onFailure = {
                Log.e("QuestionRecyclerViewAdapter", "put unsuccessfully")
            })
    }

    fun deleteItem(position: Int) {
        ConstantsQuestionFunction.deleteQuestion(activity, questionModels[position]._id.toString(),
            onSuccess = {
                Log.e("QuestionRecyclerViewAdapter", "delete successfully")
                questionModels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, questionModels.size)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "delete unsuccessfully")
            })
    }

    fun moveItem(position: Int) {
        questionModels.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, questionModels.size)
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        var tvQuestionTitle: TextView
        var tvQuestionType: TextView
        var tvQuestionCreatedDate: TextView
        var btnEditQuestion: ImageButton

        init {
//          TODO: bankID
            tvQuestionTitle = itemView.findViewById(R.id.question_title)
            tvQuestionType = itemView.findViewById(R.id.question_type)
            tvQuestionCreatedDate = itemView.findViewById(R.id.question_createdDate)
            btnEditQuestion = itemView.findViewById(R.id.btn_edit)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
            btnEditQuestion.setOnClickListener { recyclerViewInterface.settingCard(adapterPosition) }
        }
    }
}