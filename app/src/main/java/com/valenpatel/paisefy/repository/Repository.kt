package com.valenpatel.paisefy.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.valenpatel.paisefy.db.dao.DataArchiveDAO
import com.valenpatel.paisefy.db.dao.DataRecentlyDeletedDAO
import com.valenpatel.paisefy.db.dao.TodosDAO
import com.valenpatel.paisefy.db.dao.TransactionDAO
import com.valenpatel.paisefy.db.database.TransactionDatabase
import com.valenpatel.paisefy.db.entities.ArchivedTransaction
import com.valenpatel.paisefy.db.entities.RecentlyDeletedDataEntity
import com.valenpatel.paisefy.db.entities.TodoEntity
import com.valenpatel.paisefy.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class Repository(application: Application) {


    private val transactionDAO: TransactionDAO
    private val todosDAO: TodosDAO
    private val dataArchiveDAO: DataArchiveDAO
    private val recentlyDeletedDataDAO: DataRecentlyDeletedDAO

    val allTransactions: LiveData<List<Transaction>>
    val allTodos: LiveData<List<TodoEntity>>
    val archivedTransactions: LiveData<List<ArchivedTransaction>>
    val recentlyDeletedData: LiveData<List<RecentlyDeletedDataEntity>>

    init {
        val database = TransactionDatabase.getDatabase(application)
        transactionDAO = database.transactionDao()
        todosDAO = database.todoDao()
        dataArchiveDAO = database.dataArchiveDao()
        recentlyDeletedDataDAO = database.recentlyDeletedDataDao()

        allTransactions = transactionDAO.getAllDataTransaction()
        allTodos = todosDAO.getAllTodos()
        archivedTransactions = dataArchiveDAO.getAll()
        recentlyDeletedData = recentlyDeletedDataDAO.getAll()

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


    //Todos
    suspend fun insertTodosRepository(todoEntity: TodoEntity) {
        todosDAO.insertTodo(todoEntity)
    }

    suspend fun updateTodosRepository(todoEntity: TodoEntity) {
        todosDAO.updateTodo(todoEntity)
    }

    suspend fun deleteTodosRepository(todoEntity: TodoEntity) {
        todosDAO.deleteTodos(todoEntity)
    }

    fun getTodoById(id: Int): LiveData<TodoEntity> {
        return todosDAO.getTodosById(id)
    }


    suspend fun archiveTransaction(transaction: Transaction) {
        val archivedTransaction = ArchivedTransaction(
            transType = transaction.transType,
            category = transaction.category,
            account = transaction.account,
            note = transaction.note,
            date = transaction.date,
            amount = transaction.amount,
            isExpandable = transaction.isExpandable
        )

        // Use IO dispatcher for database operation
        withContext(Dispatchers.IO) {
            dataArchiveDAO.insertArchivedTransaction(archivedTransaction)
            transactionDAO.deleteTransaction(transaction)
        }
    }


    fun getAllArchivedTransactions(): LiveData<List<ArchivedTransaction>> {
        return dataArchiveDAO.getAll()
    }


    suspend fun recentlyDeletedTransaction(transaction: Transaction) {
        val recentlyDeletedTransaction = RecentlyDeletedDataEntity(
            transType = transaction.transType,
            category = transaction.category,
            account = transaction.account,
            note = transaction.note,
            date = transaction.date,
            amount = transaction.amount,
            isExpandable = transaction.isExpandable
        )

        // Use IO dispatcher for database operation
        withContext(Dispatchers.IO) {
            recentlyDeletedDataDAO.insertDeletedTransaction(recentlyDeletedTransaction)
            transactionDAO.deleteTransaction(transaction)

        }
    }


//Category
// Methods to get category counts by date
//fun getIncomeCategoriesByDate(type:String,date: Long): LiveData<List<CategoryCount>> {
//    return transactionDAO.getCategoriesByDate(type,date)
//}
//
//    fun getExpenseCategoriesByDate(date: Long): LiveData<List<CategoryCount>> {
//        return transactionDAO.getExpenseCategoriesByDate(date)
//    }
//
//    // Methods to get category counts by month
//    fun getIncomeCategoriesByMonth(month: Int, year: Int): LiveData<List<CategoryCount>> {
//        val startTimestamp = calculateStartOfMonthTimestamp(month, year)
//        val endTimestamp = calculateEndOfMonthTimestamp(month, year)
//        return transactionDAO.getIncomeCategoriesByMonth(startTimestamp, endTimestamp)
//    }
//
//    fun getExpenseCategoriesByMonth(month: Int, year: Int): LiveData<List<CategoryCount>> {
//        val startTimestamp = calculateStartOfMonthTimestamp(month, year)
//        val endTimestamp = calculateEndOfMonthTimestamp(month, year)
//        return transactionDAO.getExpenseCategoriesByMonth(startTimestamp, endTimestamp)
//    }
}