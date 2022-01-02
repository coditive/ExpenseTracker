package com.syrous.expensetracker.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.datainterface.TransactionManagerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject




@HiltViewModel
class ActivityMainVM @Inject constructor(transactionDao: TransactionDao): ViewModel() {

    private val transactionManager: TransactionManager = TransactionManagerImpl(transactionDao, viewModelScope)

    val transactionsList = transactionManager
        .getAllTransactionsFromStorage()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}