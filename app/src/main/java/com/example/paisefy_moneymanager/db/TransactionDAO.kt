package com.example.paisefy_moneymanager.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.paisefy_moneymanager.model.CategoryCount
import com.example.paisefy_moneymanager.model.Transaction
import java.util.Date

@Dao
interface TransactionDAO{
    @Query("SELECT * FROM transactions")
    fun getAllDataTransaction(): LiveData<List<Transaction>>

    @Insert
    fun insertTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE date = :date")
    fun getTransactionsByDate(date: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startTimestamp AND date < :endTimestamp")
    fun getTransactionsByTimestampRange(startTimestamp: Long, endTimestamp: Long): LiveData<List<Transaction>>

    //Category
    // Query to get category counts for a specific date for income
    @Query("SELECT category, COUNT(*) as count FROM transactions WHERE transType = :type AND date = :date GROUP BY category")
    fun getCategoriesByDate(type: String, date: Long): LiveData<List<CategoryCount>>

    // Query to get category counts for a specific date for expense
    @Query("SELECT category, COUNT(*) as count FROM transactions WHERE transType = 'EXPENSE' AND date = :date GROUP BY category")
    fun getExpenseCategoriesByDate(date: Long): LiveData<List<CategoryCount>>

    // Query to get category counts for a specific month for income
    @Query("SELECT category, COUNT(*) as count FROM transactions WHERE transType = 'INCOME' AND date >= :startTimestamp AND date <= :endTimestamp GROUP BY category")
    fun getIncomeCategoriesByMonth(startTimestamp: Long, endTimestamp: Long): LiveData<List<CategoryCount>>

    // Query to get category counts for a specific month for expense
    @Query("SELECT category, COUNT(*) as count FROM transactions WHERE transType = 'EXPENSE' AND date >= :startTimestamp AND date <= :endTimestamp GROUP BY category")
    fun getExpenseCategoriesByMonth(startTimestamp: Long, endTimestamp: Long): LiveData<List<CategoryCount>>
}