package com.valenpatel.paisefy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.valenpatel.paisefy.db.entities.RecentlyDeletedDataEntity


@Dao
interface DataRecentlyDeletedDAO {
    @Query("SELECT * FROM recently_deleted_data")
    fun getAll(): LiveData<List<RecentlyDeletedDataEntity>>

    @Insert
    fun insertDeletedTransaction(transaction: RecentlyDeletedDataEntity)

    @Delete
    fun deleteTransactionInserted(transaction: RecentlyDeletedDataEntity)
}