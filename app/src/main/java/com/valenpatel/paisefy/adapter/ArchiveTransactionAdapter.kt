package com.valenpatel.paisefy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.ArchiveTransactionLayoutBinding
import com.valenpatel.paisefy.db.entities.ArchivedTransaction
import com.valenpatel.paisefy.utils.Constants
import com.valenpatel.paisefy.utils.Helper

class ArchiveTransactionAdapter(
    val context: Context,
    val archivedTransaction: MutableList<ArchivedTransaction>
) :
    RecyclerView.Adapter<ArchiveTransactionAdapter.ArchiveTransactionViewHolder>() {

    class ArchiveTransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ArchiveTransactionLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArchiveTransactionAdapter.ArchiveTransactionViewHolder {
       return ArchiveTransactionViewHolder( ArchiveTransactionLayoutBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun onBindViewHolder(
        holder: ArchiveTransactionAdapter.ArchiveTransactionViewHolder,
        position: Int
    ) {
        val archivedTransaction = archivedTransaction[position]

        holder.binding.apply {
            transactionAmount.text = "₹" + archivedTransaction.amount.toString()
            transcationCategory.text = archivedTransaction.category
            accountLabel.text = archivedTransaction.account
            transactionDate.text = Helper.formateDate(archivedTransaction.date)

        }
        //category icon
        val transactionCategory = Constants.getCategoryDetails(archivedTransaction.category)
        if (archivedTransaction != null) {
            if (transactionCategory != null) {
                holder.binding.categoryIconImage.setImageResource(transactionCategory.categoryImage)
            }
            if (transactionCategory != null) {
                holder.binding.categoryIconImage.backgroundTintList =
                    context.getColorStateList(transactionCategory.categoryColor)
            }
        }

        // transaction Color
        if (archivedTransaction.transType.equals(Constants.EXPENSE)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.red))
        } else if (archivedTransaction.transType.equals(Constants.INCOME)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.income))
        }

    }

    override fun getItemCount(): Int {
      return archivedTransaction.size
    }

}