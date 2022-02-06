package com.syrous.expensetracker.screen.viewtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.datainterface.SubCategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
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
    private val subCategoryManager: SubCategoryManager
) : ViewModel(), ViewTransactionVM {

    override val transactionsList: StateFlow<List<TransactionHeaderItem>>
        get() = TODO()

    override val categoryTagList: StateFlow<List<String>>
        get() = subCategoryManager.getExpenseSubCategoriesFlow()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override fun setCurrentSelectCategoryItemId(categoryId: Int) {
        TODO("Not yet implemented")
    }

}