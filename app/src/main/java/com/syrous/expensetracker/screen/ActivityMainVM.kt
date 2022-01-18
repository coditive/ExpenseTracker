package com.syrous.expensetracker.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.ApiRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.datainterface.TransactionManagerImpl
import com.syrous.expensetracker.upload.SearchOrCreateAppFolderUseCase
import com.syrous.expensetracker.upload.UploadUserTransactionUseCase
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ActivityMainVM @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    transactionDao: TransactionDao,
    apiRequest: ApiRequest
) : ViewModel() {

    private var transactionManager: TransactionManager

    var transactionsList: StateFlow<List<UserTransaction>>

    private val searchUseCase = SearchOrCreateAppFolderUseCase(apiRequest, sharedPrefManager, viewModelScope)

    private val uploadUseCase = UploadUserTransactionUseCase(transactionDao, apiRequest)

    @RequiresApi(Build.VERSION_CODES.R)
    fun searchFolderOrCreate(context: Context) {
        viewModelScope.launch(Dispatchers.IO){
            uploadUseCase.uploadUserTransactionToDrive(context, sharedPrefManager.getUserToken(),
                Constants.apiKey, "test-file")
        }
    }

    init {
        transactionManager = TransactionManagerImpl(transactionDao, apiRequest, viewModelScope)

        viewModelScope.launch(Dispatchers.IO) {
            if (sharedPrefManager.isNewUser()) {
                sharedPrefManager.makeUserRegular()
            }
        }

        transactionsList =  transactionManager
                .getAllTransactionsFromStorage()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

}