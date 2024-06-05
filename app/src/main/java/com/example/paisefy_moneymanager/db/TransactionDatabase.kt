package com.example.paisefy_moneymanager.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Converters

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  //Type Converter is in util class converting date to long and vice versa
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null
        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, "transaction_database")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

