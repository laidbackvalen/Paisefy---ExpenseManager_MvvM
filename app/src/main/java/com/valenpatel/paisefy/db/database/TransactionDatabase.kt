package com.valenpatel.paisefy.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valenpatel.paisefy.db.dao.DataArchiveDAO
import com.valenpatel.paisefy.db.dao.DataRecentlyDeletedDAO
import com.valenpatel.paisefy.db.dao.TodosDAO
import com.valenpatel.paisefy.db.dao.TransactionDAO
import com.valenpatel.paisefy.db.entities.ArchivedTransaction
import com.valenpatel.paisefy.db.entities.RecentlyDeletedDataEntity
import com.valenpatel.paisefy.db.entities.TodoEntity
import com.valenpatel.paisefy.model.Transaction
import com.valenpatel.paisefy.utils.Converters


@Database(entities = [Transaction::class, TodoEntity::class, ArchivedTransaction::class, RecentlyDeletedDataEntity::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO
    abstract fun todoDao(): TodosDAO
    abstract fun dataArchiveDao(): DataArchiveDAO
    abstract fun recentlyDeletedDataDao(): DataRecentlyDeletedDAO

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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the archived_transactions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS archived_transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        transType TEXT NOT NULL,
                        category TEXT NOT NULL,
                        account TEXT NOT NULL,
                        note TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        expandBoolean INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN image BLOB DEFAULT NULL")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE archived_transactions ADD COLUMN image BLOB DEFAULT NULL")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS recently_deleted_data (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        transType TEXT NOT NULL,
                        category TEXT NOT NULL,
                        account TEXT NOT NULL,
                        note TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        expandBoolean INTEGER NOT NULL DEFAULT 0,
                        image BLOB DEFAULT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, "transaction_database")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}