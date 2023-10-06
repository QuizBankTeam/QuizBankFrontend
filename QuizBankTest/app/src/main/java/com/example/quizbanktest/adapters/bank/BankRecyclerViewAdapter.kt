package com.example.quizbanktest.adapters.bank

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizbanktest.R
import com.example.quizbanktest.fragment.interfaces.RecyclerViewInterface
import com.example.quizbanktest.models.QuestionBankModel
import com.example.quizbanktest.utils.ConstantsQuestionBankFunction
import java.util.*

class BankRecyclerViewAdapter(var context: Context,
                              var activity: AppCompatActivity,
                              var questionBankModels: ArrayList<QuestionBankModel>,
                              var recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder>() {
    /** the arrayList that recyclerView use */
    var arrayList: ArrayList<QuestionBankModel> = questionBankModels
    /** store the original status of arrayList, which means questionBankModels above */
    var arrayListFilter: ArrayList<QuestionBankModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        try {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_bankcard, parent, false)
            arrayListFilter.clear()
            arrayListFilter.addAll(arrayList)
            return MyViewHolder(view, recyclerViewInterface)
        } catch (e: Exception) {
            Log.e("BankRecyclerViewAdapter", "onCreateView", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvBankTitle.text = questionBankModels[position].title
        holder.tvBankCreatedDate.text = questionBankModels[position].createdDate

    }

    override fun getItemCount(): Int { return questionBankModels.size }

    fun getFilter(): Filter { return mFilter }

    /** use Filter() to filter the words */
    private var mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            /** the arrayList used to copy original arrayList */
            val filteredList: ArrayList<QuestionBankModel> = ArrayList()

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
            arrayList.addAll(results.values as Collection<QuestionBankModel>)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(data: QuestionBankModel) {
        questionBankModels.add(data)
        notifyDataSetChanged()
    }

    fun setItem(position: Int, data: QuestionBankModel) {
        ConstantsQuestionBankFunction.putQuestionBank(activity, data,
            onSuccess = {
                Log.e("BankRecyclerViewAdapter", "put successfully")
                questionBankModels[position] = data
                notifyItemChanged(position)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "put unsuccessfully")
            })
    }

    fun deleteItem(position: Int) {
        ConstantsQuestionBankFunction.deleteQuestionBank(activity, questionBankModels[position]._id,
            onSuccess = {
                Log.e("BankRecyclerViewAdapter", "delete successfully")
                questionBankModels.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, questionBankModels.size)
            },
            onFailure = {
                Log.e("BankRecyclerViewAdapter", "delete unsuccessfully")
            })
    }

    class MyViewHolder(itemView: View, recyclerViewInterface: RecyclerViewInterface) : RecyclerView.ViewHolder(itemView) {
        var tvBankTitle: TextView
        var tvBankCreatedDate: TextView
        var btnEditBank: ImageButton

        init {
            tvBankTitle = itemView.findViewById(R.id.bank_title)
            tvBankCreatedDate = itemView.findViewById(R.id.bank_createdDate)
            btnEditBank = itemView.findViewById(R.id.btn_edit)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(position)
                }
            }
            btnEditBank.setOnClickListener { recyclerViewInterface.settingCard(adapterPosition) }
        }
    }
}