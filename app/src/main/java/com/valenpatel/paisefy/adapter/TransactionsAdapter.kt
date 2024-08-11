package com.valenpatel.paisefy.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.RowTransactionsBinding
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Constants
import com.valenpatel.paisefy.utils.Helper


class TransactionsAdapter(val context: Context, var transactions: MutableList<Transaction>, private val itemClickListener: OnItemClickedListener) :
    RecyclerView.Adapter<TransactionsAdapter.TransacationViewHolder>() {

   //sending data from adapter to fragment using interface and parcelable class
    interface OnItemClickedListener {
            fun onItemClicked(transaction: Transaction) {
        }
    }

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions.toMutableList() //adding data coming from live data in transaction
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
        holder.binding.transactionAmount.text = "₹" + transaction.amount.toString()
        holder.binding.transcationCategory.text = transaction.category
        holder.binding.accountLabel.text = transaction.account
        Glide
            .with(this.context)
            .load(transaction.image)
            .fitCenter()
            .placeholder(R.drawable.baseline_image_24)
            .into(holder.binding.imageView3);

        //view button
        holder.binding.viewUpdate.setOnClickListener {
            itemClickListener.onItemClicked(transaction)
        }

        //share button
        holder.binding.viewShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            val textToShare = " Category : " + transaction.category +"\nAccount : " + transaction.account+ "\nType : " + transaction.transType +
                    "\nDate : " + Helper.formateDate(
                    transaction.date) + "\nAmount : ₹" + transaction.amount+ "\nNote : "+ transaction.note
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            holder.itemView.context.startActivity(Intent.createChooser(intent, "Share Text via"))
        }

        //category icon
        val transactionCategory = Constants.getCategoryDetails(transaction.category)
        if (transactionCategory != null) {
            holder.binding.categoryIconImage.setImageResource(transactionCategory.categoryImage)
            holder.binding.categoryIconImage.backgroundTintList =
                context.getColorStateList(transactionCategory.categoryColor)
        }

        // transaction Color
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

    // Remove item at a specific position
    fun removeAt(position: Int) {
        transactions.removeAt(position)
        notifyItemRemoved(position)
    }

    // Add item at a specific position
    fun add(position: Int, transaction: Transaction) {
        transactions.add(position, transaction)
        notifyItemInserted(position)
    }

    // Implement swipe-to-delete functionality
    fun getSwipableViewHolder(viewHolder: RecyclerView.ViewHolder): RecyclerView.ViewHolder =
        viewHolder
}
private fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
