package com.example.paisefy_moneymanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.paisefy_moneymanager.model.Transaction
import com.example.paisefy_moneymanager.repository.TransactionRepository
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

    init {
        repository = TransactionRepository(application)
        transactionList = repository.allTransactions
    }

    fun selectTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }

    fun vmInsertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransactionRepository(transaction)
    }

    fun vmUpdateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransactionRepository(transaction)
    }

    fun vmDeleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransactionRepository(transaction)
    }


}