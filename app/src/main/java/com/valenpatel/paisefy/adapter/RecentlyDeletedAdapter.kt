package com.valenpatel.paisefy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.RecentlyDeletedItemsLayoutBinding
import com.valenpatel.paisefy.db.entities.RecentlyDeletedDataEntity
import com.valenpatel.paisefy.utils.Constants
import com.valenpatel.paisefy.utils.Helper

class RecentlyDeletedAdapter(
    val context: Context,
    val recentlyDeletedData: MutableList<RecentlyDeletedDataEntity>
) :
    RecyclerView.Adapter<RecentlyDeletedAdapter.RecentlyDeletedViewHolder>() {
    class RecentlyDeletedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RecentlyDeletedItemsLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyDeletedAdapter.RecentlyDeletedViewHolder {
        return RecentlyDeletedViewHolder(
            RecentlyDeletedItemsLayoutBinding.inflate(LayoutInflater.from(context), parent, false).root
        )
    }

    override fun onBindViewHolder(holder: RecentlyDeletedAdapter.RecentlyDeletedViewHolder, position: Int) {
        val recentlyDeletedData = recentlyDeletedData[position]

        holder.binding.apply {
            transactionAmount.text = "₹" + recentlyDeletedData.amount.toString()
            transcationCategory.text = recentlyDeletedData.category
            accountLabel.text = recentlyDeletedData.account
            transactionDate.text = Helper.formateDate(recentlyDeletedData.date)

        }
        //category icon
        val transactionCategory = Constants.getCategoryDetails(recentlyDeletedData.category)
        if (recentlyDeletedData != null) {
            if (transactionCategory != null) {
                holder.binding.categoryIconImage.setImageResource(transactionCategory.categoryImage)
            }
            if (transactionCategory != null) {
                holder.binding.categoryIconImage.backgroundTintList =
                    context.getColorStateList(transactionCategory.categoryColor)
            }
        }

        // transaction Color
        if (recentlyDeletedData.transType.equals(Constants.EXPENSE)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.red))
        } else if (recentlyDeletedData.transType.equals(Constants.INCOME)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.income))
        }

    }

    override fun getItemCount(): Int {
        return recentlyDeletedData.size
    }
}