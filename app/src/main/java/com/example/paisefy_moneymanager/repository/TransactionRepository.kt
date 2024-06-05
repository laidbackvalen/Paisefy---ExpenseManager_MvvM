package com.example.paisefy_moneymanager.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.paisefy_moneymanager.db.TransactionDAO
import com.example.paisefy_moneymanager.db.TransactionDatabase
import com.example.paisefy_moneymanager.model.Transaction
import java.util.Calendar

class TransactionRepository(application: Application) {

    private val transactionDAO: TransactionDAO
    val allTransactions: LiveData<List<Transaction>>

    init {
        val database = TransactionDatabase.getDatabase(application)
        transactionDAO = database.transactionDao()
        allTransactions = transactionDAO.getAllDataTransaction()
    }

    suspend fun insertTransactionRepository(transaction: Transaction) {
        transactionDAO.insertTransaction(transaction)
    }

    suspend fun updateTransactionRepository(transaction: Transaction) {
        transactionDAO.updateTransaction(transaction)
    }

    suspend fun deleteTransactionRepository(transaction: Transaction) {
        transactionDAO.deleteTransaction(transaction)
    }
    fun getTransactionsByDate(date: Long): LiveData<List<Transaction>> {
        return transactionDAO.getTransactionsByDate(date)
    }
    fun getTransactionsByMonth(month: Int, year: Int): LiveData<List<Transaction>> {
        // Calculate the start and end timestamps for the specified month and year
        val startTimestamp = calculateStartOfMonthTimestamp(month, year)
        val endTimestamp = calculateEndOfMonthTimestamp(month, year)

        // Call the DAO method to fetch transactions within the specified timestamp range
        return transactionDAO.getTransactionsByTimestampRange(startTimestamp, endTimestamp)
    }

    private fun calculateStartOfMonthTimestamp(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar.MONTH is zero-based
            set(Calendar.DAY_OF_MONTH, 1) // Set day to 1st of the month
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun calculateEndOfMonthTimestamp(month: Int, year: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar.MONTH is zero-based
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) // Get the last day of the month
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

}