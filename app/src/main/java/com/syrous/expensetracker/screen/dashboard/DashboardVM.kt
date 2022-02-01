package com.syrous.expensetracker.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.User
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.GetDashboardSubCategoryItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface DashboardVM {

    val totalExpense: StateFlow<Int>

    val topCategoriesList: StateFlow<List<DashboardCategoryItem>>

    val categorizedUserTransactionList: StateFlow<List<UserTransaction>>

    fun setCurrentCategoryItemId(categoryId: Int)
}

@HiltViewModel
class DashboardVMImpl @Inject constructor(
    private val getDashboardSubCategoryItemUseCase: GetDashboardSubCategoryItemUseCase,
    private val transactionManager: TransactionManager
) : ViewModel(), DashboardVM {

    private val categoryIdFlow = MutableStateFlow(0)

    override val totalExpense: StateFlow<Int>
        get() = transactionManager.getTotalExpenseAmount()
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    override val topCategoriesList: StateFlow<List<DashboardCategoryItem>>
        get() = getDashboardSubCategoryItemUseCase.execute()
            .take(5)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override val categorizedUserTransactionList: StateFlow<List<UserTransaction>>
        get() = categoryIdFlow.flatMapLatest { categoryId ->
            transactionManager.getSubCategorisedUserTransactions(categoryId)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    override fun setCurrentCategoryItemId(categoryId: Int) {
        categoryIdFlow.value = categoryId
        Log.d("Dashboardvm", "$categoryId")
    }


}