package com.syrous.expensetracker.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.SubCategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import com.syrous.expensetracker.utils.toTransactionHeader
import com.syrous.expensetracker.utils.toTransactionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


interface ReleaseVM {
    val transactionsList: StateFlow<List<TransactionHeaderItem>>

    val totalExpense: StateFlow<Int>

    val pieChartItemsList: StateFlow<List<SubCategoryItem>>

    fun pieValueSelected(categoryTag: String)

    fun nothingSelected()
}

@HiltViewModel
class ReleaseVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val subCategoryManager: SubCategoryManager,
    private val sharedPrefManager: SharedPrefManager,
) : ViewModel(), ReleaseVM {

    private val dateFormatter = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    private val categoryTagFlow = MutableStateFlow("")
    private val subCategoryType = MutableStateFlow(Category.EXPENSE)

    override val transactionsList: StateFlow<List<TransactionHeaderItem>>
        get() = transactionManager.getAllTransactionsFromStorage()
            .combine(categoryTagFlow) { userTransactionList, categoryTag ->
                if (categoryTag.isEmpty()) userTransactionList
                else userTransactionList.filter { transaction ->
                    transaction.categoryTag == categoryTag
                }
            }.map { userTransactionList ->
                val transactionHeaderItemList = mutableListOf<TransactionHeaderItem>()
                userTransactionList.forEachIndexed { index, transaction ->
                    when {
                        index == 0 -> {
                            transactionHeaderItemList.add(transaction.toTransactionHeader())
                            transactionHeaderItemList.add(transaction.toTransactionItem())
                        }
                        dateFormatter.format(userTransactionList[index - 1].date) != dateFormatter.format(
                            userTransactionList[index].date
                        ) -> {
                            transactionHeaderItemList.add(transaction.toTransactionHeader())
                            transactionHeaderItemList.add(transaction.toTransactionItem())
                        }
                        else -> transactionHeaderItemList.add(transaction.toTransactionItem())
                    }
                }
                transactionHeaderItemList
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override val totalExpense: StateFlow<Int>
        get() = transactionManager.getTotalExpenseAmount()
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    override val pieChartItemsList: StateFlow<List<SubCategoryItem>>
        get() = combine(
            subCategoryType,
            subCategoryManager.getExpenseSubCategoriesFlow(),
            subCategoryManager.getIncomeSubCategoriesFlow()
        ) { category, expenseSubCategory, incomeSubCategory ->
            if (category == Category.EXPENSE)
                expenseSubCategory
            else
                incomeSubCategory
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    override fun pieValueSelected(categoryTag: String) {
        Log.d("categoryFlow", categoryTag)
        categoryTagFlow.value = categoryTag
    }

    override fun nothingSelected() {
        categoryTagFlow.value = ""
    }
}