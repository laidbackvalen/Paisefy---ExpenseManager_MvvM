package com.valenpatel.paisefy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.valenpatel.paisefy.db.entities.ArchivedTransaction


@Dao
interface DataArchiveDAO {
    @Query("SELECT * FROM archived_transactions")
    fun getAll(): LiveData<List<ArchivedTransaction>>

    @Insert
    fun insertArchivedTransaction(transaction: ArchivedTransaction)

    @Delete
    fun deleteArchivedTransaction(transaction: ArchivedTransaction)

}