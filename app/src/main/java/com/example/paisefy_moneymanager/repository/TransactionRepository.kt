package com.example.paisefy_moneymanager.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.paisefy_moneymanager.db.TransactionDAO
import com.example.paisefy_moneymanager.db.TransactionDatabase
import com.example.paisefy_moneymanager.model.Transaction

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



}