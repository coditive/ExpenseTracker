package com.syrous.expensetracker.screen.viewtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionHeader
import com.syrous.expensetracker.screen.viewtransaction.TransactionHeaderItem.TransactionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


interface ViewTransactionVM {

    val transactionsList: StateFlow<List<TransactionHeaderItem>>

    val categoryTagList: StateFlow<List<String>>

    fun setCurrentSelectCategoryItemId(categoryId: Int)
}


@HiltViewModel
class ViewTransactionVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val categoryManager: CategoryManager
) : ViewModel(), ViewTransactionVM {

    override val transactionsList: StateFlow<List<TransactionHeaderItem>>
        get() = TODO()

    override val categoryTagList: StateFlow<List<String>>
        get() = categoryManager.getExpenseSubCategoriesFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override fun setCurrentSelectCategoryItemId(categoryId: Int) {
        TODO("Not yet implemented")
    }

}