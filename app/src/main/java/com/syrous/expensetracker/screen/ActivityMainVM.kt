package com.syrous.expensetracker.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.screen.MainActivityViewState.Success.Companion
import com.syrous.expensetracker.screen.usertransaction.ViewState
import com.syrous.expensetracker.usecase.*
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


interface ActivityMainVM {
    val viewState: StateFlow<MainActivityViewState>

    val userTransactionFlow: StateFlow<List<UserTransaction>>

}

@HiltViewModel
class ActivityMainVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val sharedPrefManager: SharedPrefManager,
    private val searchUseCase: SearchOrCreateAppFolderUseCase,
    private val uploadUseCase: UploadUserTransactionUseCase,
    private val createSheetsUseCase: CreateSheetsUseCase,
    private val modifySheetToTemplateUseCase: ModifySheetToTemplateUseCase,
    private val appendTransactionsUseCase: AppendTransactionsUseCase
) : ViewModel(), ActivityMainVM {

    private val _viewState = MutableStateFlow(MainActivityViewState.Success.defaultState())
    override val viewState: StateFlow<MainActivityViewState>
        get() = _viewState

    override val userTransactionFlow: StateFlow<List<UserTransaction>>
        get() = transactionManager.getAllTransactionsFromStorage()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.R)
    fun searchFolderOrCreate(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sharedPrefManager.isFileUploadStatus()) {
                searchUseCase.execute()
                delay(100)
                uploadUseCase.uploadUserTransactionToDrive(
                    context,
                    "Total-Expense-Sheet.csv",
                    "All in one sheet for expenses"
                )
                delay(100)
                createSheetsUseCase.execute()
                delay(100)
                modifySheetToTemplateUseCase.execute(context)
                sharedPrefManager.storeFileUploadStatus(true)
            } else {
                appendTransactionsUseCase.execute()
            }
        }
    }

}

sealed class MainActivityViewState {

    data class Success(
        val category: Category,
        val date: Date,
        val categoryTag: String,
        val description: String,
        val amount: Int,
    ) : MainActivityViewState() {
        companion object {
            fun defaultState(): Success {
                return Success(
                    Category.EXPENSE,
                    Date(),
                    categoryTag = "",
                    description = "",
                    amount = 0,
                )
            }
        }
    }

    data class Error(val element: String, val errorMessage: String) : MainActivityViewState()
}