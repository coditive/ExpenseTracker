package com.syrous.expensetracker.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.datainterface.TransactionManagerImpl
import com.syrous.expensetracker.upload.UploadUserTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class ActivityMainVM @Inject constructor(
    transactionDao: TransactionDao,
    private val useCase: UploadUserTransactionUseCase
): ViewModel() {

    private val transactionManager: TransactionManager = TransactionManagerImpl(transactionDao, viewModelScope)

    val transactionsList = transactionManager
        .getAllTransactionsFromStorage()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.R)
    fun exportDataToDrive(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.uploadUserTransactionToDrive(context, "TestCSV")
        }
    }

}