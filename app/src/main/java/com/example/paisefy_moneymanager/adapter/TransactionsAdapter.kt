package com.example.paisefy_moneymanager.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.databinding.RowTransactionsBinding
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Constants
import com.example.paisefy_moneymanager.utils.Helper

class TransactionsAdapter(val context: Context, var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransacationViewHolder>() {


    fun updateTransactions(newTransactions : List<Transaction>) {
        transactions = newTransactions //adding data coming from live data in transaction
        notifyDataSetChanged()
    }

    class TransacationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RowTransactionsBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacationViewHolder {
        return TransacationViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_transactions, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransacationViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.binding.transactionDate.text = Helper.formateDate(transaction.date)
        holder.binding.transactionAmount.text = "₹"+transaction.amount.toString()
        holder.binding.transcationCategory.text = transaction.category
        holder.binding.accountLabel.text = transaction.account

        val transactionCategory = Constants.getCategoryDetails(transaction.category)
        if (transactionCategory != null) {
            holder.binding.categoryIconImage.setImageResource(transactionCategory.categoryImage)
            holder.binding.categoryIconImage.backgroundTintList = context.getColorStateList(transactionCategory.categoryColor)
        }

        if (transaction.transType.equals(Constants.EXPENSE)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.red))
        } else if (transaction.transType.equals(Constants.INCOME)) {
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.income))
        }
        holder.binding.notes.text = transaction.note

        // Handle expandable state
        val isExpandable = transaction.isExpandable
        holder.binding.constraintLayoutRT.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.binding.constraint.setOnClickListener {
            transaction.isExpandable = !transaction.isExpandable
            notifyItemChanged(position)
        }

    }



    // Implement swipe-to-delete functionality
    fun getSwipableViewHolder(viewHolder: RecyclerView.ViewHolder): RecyclerView.ViewHolder = viewHolder
}