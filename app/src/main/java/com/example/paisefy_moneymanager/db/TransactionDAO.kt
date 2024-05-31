package com.example.paisefy_moneymanager.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.paisefy_moneymanager.model.Transaction

@Dao
interface TransactionDAO {
    @Query("SELECT * FROM transactions")
    fun getAllDataTransaction(): LiveData<List<Transaction>>

    @Insert
    fun insertTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)


}