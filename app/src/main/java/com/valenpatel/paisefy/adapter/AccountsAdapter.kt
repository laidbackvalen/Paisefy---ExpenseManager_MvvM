package com.valenpatel.paisefy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.databinding.RowAccountBinding
import com.valenpatel.paisefy.model.Account

class AccountsAdapter(val context: Context, val accounts: List<Account>, val accountTypeClickListener: AccountTypeClickListener) :
    RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {

    interface AccountTypeClickListener {
        fun onAccountTypeClick(accountType: String)
    }

    class AccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RowAccountBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        return AccountsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_account, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = accounts[position]
        holder.binding.accountName.setText(account.accountName)

        holder.itemView.setOnClickListener {
            accountTypeClickListener.onAccountTypeClick(account.accountName)
        }
    }

}