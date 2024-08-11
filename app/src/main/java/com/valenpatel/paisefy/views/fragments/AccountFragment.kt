package com.valenpatel.paisefy.views.fragments

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.valenpatel.paisefy.databinding.FragmentAccountBinding
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Helper
import com.valenpatel.paisefy.viewmodel.MainViewModel
import com.valenpatel.paisefy.views.activities.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: MainViewModel
    var milliseconds: Long = 0

    // Tab
    var selectedTab = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        viewModel = (activity as MainActivity).viewModel

        val calendar = Calendar.getInstance()
        val dateString = Helper.formateDate(calendar.time)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        milliseconds = date?.time ?: -1

        setupTabLayout()
        observeTransactions()
        updateTransactionsForDate(milliseconds)
        return binding.root
    }

    private fun setupTabLayout() {
        binding.tabLayoutAcc.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "Monthly" -> {
                        selectedTab = 1
                        fetchMonthlyTransactions()
                    }
                    "Daily" -> {
                        selectedTab = 0
                        updateTransactionsForDate(milliseconds)
                    }
                    "All" -> {
                        selectedTab = 2
                        accountWiseAllTransactions()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeTransactions() {
        viewModel.transactionList.observe(viewLifecycleOwner, Observer { transactions ->
            if (transactions.isEmpty()) {
                Toast.makeText(requireContext(), "No Transactions", Toast.LENGTH_SHORT).show()
            }
            updateTransactionAmounts(transactions)
        })
    }

    private fun updateTransactionAmounts(transactions: List<Transaction>) {
        var incomeAmount = 0.0
        var expenseAmount = 0.0

        transactions.forEach {
            if (it.transType == "INCOME") {
                incomeAmount += it.amount
            } else if (it.transType == "EXPENSE") {
                expenseAmount += it.amount
            }
        }

        binding.incomeAmountText.text = "₹ $incomeAmount"
        binding.expenseAmountText.text = "₹ $expenseAmount"
        binding.totalAmountText.text = "₹ ${incomeAmount - expenseAmount}"

        updateAccountAmounts(transactions, "Cash", binding.cashIncomeAmountText, binding.cashExpenseAmountText)
        updateAccountAmounts(transactions, "BANK / NEFT / RTGS / CHEQUE / DRAFT", binding.bankIncomeAmountText, binding.bankExpenseAmountText)
        updateAccountAmounts(transactions, "UPI", binding.upiAmountText, binding.upiExpenseAmountText)
        updateAccountAmounts(transactions, "Other", binding.otherAmountText, binding.otherExpenseAmountText)
    }

    private fun updateAccountAmounts(transactions: List<Transaction>, accountType: String, incomeTextView: TextView, expenseTextView: TextView) {
        var incomeAmount = 0.0
        var expenseAmount = 0.0

        transactions.forEach { transaction ->
            if (transaction.account == accountType) {
                if (transaction.transType == "INCOME") {
                    incomeAmount += transaction.amount
                } else if (transaction.transType == "EXPENSE") {
                    expenseAmount += transaction.amount
                }
            }
        }

        incomeTextView.text = "₹ $incomeAmount"
        expenseTextView.text = "₹ $expenseAmount"
    }

    private fun updateTransactionsForDate(date: Long) {
        viewModel.getTransactionsByDate(date).observe(viewLifecycleOwner, Observer { transactions ->
            if (transactions.isEmpty()) {
                Toast.makeText(requireContext(), "No Transactions", Toast.LENGTH_SHORT).show()
            }
            updateTransactionAmounts(transactions)
        })
    }

    private fun fetchMonthlyTransactions() {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is zero-based
        val year = calendar.get(Calendar.YEAR)

        viewModel.getTransactionsByMonth(month, year).observe(viewLifecycleOwner, Observer { transactions ->
            if (transactions.isEmpty()) {
                Toast.makeText(requireContext(), "No Transactions", Toast.LENGTH_SHORT).show()
            }
            updateTransactionAmounts(transactions)
        })
    }

    private fun accountWiseAllTransactions() {
        viewModel.transactionList.observe(viewLifecycleOwner, Observer { transactions ->
            if (transactions.isEmpty()) {
                Toast.makeText(requireContext(), "No Transactions", Toast.LENGTH_SHORT).show()
            }
            updateTransactionAmounts(transactions)
        })
    }
}
