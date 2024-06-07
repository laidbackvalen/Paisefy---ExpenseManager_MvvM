package com.example.paisefy_moneymanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.paisefy_moneymanager.model.CategoryCount
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    val transactionList: LiveData<List<Transaction>>

    private val _selectedTransaction = MutableLiveData<Transaction>()
    val selectedTransaction: LiveData<Transaction>
        get() = _selectedTransaction

    //NOTE :-
    //MutableLiveData is a type of LiveData that allows you to change its value.
    //LiveData is an observable data holder class that can be observed for changes,
    //but you cannot change its value directly if it's not MutableLiveData.

    //_selectedTransaction: This is the internal variable that the MainViewModel uses
    // to change the value of the selected transaction. Since it is MutableLiveData,
    // it can be updated by the ViewModel.

    //selectedTransaction: This is the external variable that other classes (e.g., UI components) can
    //observe to get updates when the selected transaction changes. It is LiveData,
    //so it cannot be modified directly by these classes.

   //_selectedTransaction (can write and update)
   //selectedTransaction (others can only read)
    init {
        repository = TransactionRepository(application)
        transactionList = repository.allTransactions
    }

    fun selectTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }
    fun getTransactionsByDate(date: Long): LiveData<List<Transaction>> {
        return repository.getTransactionsByDate(date)
    }

    fun getTransactionsByMonth(month: Int, year: Int): LiveData<List<Transaction>> {
        return repository.getTransactionsByMonth(month, year)
    }

    fun vmInsertTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTransactionRepository(transaction)
    }

    fun vmUpdateTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTransactionRepository(transaction)
    }

    fun vmDeleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTransactionRepository(transaction)
    }


    //category
    // LiveData to hold categories
    private val _incomeCategories = MutableLiveData<List<String>>()
    val incomeCategories: LiveData<List<String>>
        get() = _incomeCategories

    private val _expenseCategories = MutableLiveData<List<String>>()
    val expenseCategories: LiveData<List<String>>
        get() = _expenseCategories




    // Methods to get category counts by date
    fun getIncomeCategoriesByDate(type: String, date: Long): LiveData<List<CategoryCount>> {
        return repository.getIncomeCategoriesByDate(type,date)
    }

    fun getExpenseCategoriesByDate(date: Long): LiveData<List<CategoryCount>> {
        return repository.getExpenseCategoriesByDate(date)
    }

    // Methods to get category counts by month
    fun getIncomeCategoriesByMonth(month: Int, year: Int): LiveData<List<CategoryCount>> {
        return repository.getIncomeCategoriesByMonth(month, year)
    }

    fun getExpenseCategoriesByMonth(month: Int, year: Int): LiveData<List<CategoryCount>> {
        return repository.getExpenseCategoriesByMonth(month, year)
    }
}