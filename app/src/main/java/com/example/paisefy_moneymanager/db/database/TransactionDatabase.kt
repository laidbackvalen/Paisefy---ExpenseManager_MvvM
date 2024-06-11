package com.example.paisefy_moneymanager.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.paisefy_moneymanager.db.dao.TodosDAO
import com.example.paisefy_moneymanager.db.dao.TransactionDAO
import com.example.paisefy_moneymanager.db.entities.TodoEntity
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.utils.Converters

@Database(entities = [Transaction::class, TodoEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO
    abstract fun todoDao(): TodosDAO

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the todos table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `todos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `time` TEXT NOT NULL
                    )
                """.trimIndent())

                // Add the expandBoolean column to the transactions table
                database.execSQL("ALTER TABLE transactions ADD COLUMN expandBoolean INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}