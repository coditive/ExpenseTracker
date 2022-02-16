package com.syrous.expensetracker.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.datainterface.SubCategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


interface ExpenseTrackerWidgetVM {
    fun setTransactionCategory(category: Category)

    fun setDate(date: Date)

    fun setDescription(description: String)

    fun setAmount(amount: Int)

    fun setCategoryTag(type: String)

    fun getTagList(): StateFlow<List<SubCategoryItem>>

    fun addUserTransaction()
}


@HiltViewModel
class ExpenseTrackerWidgetVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val subCategoryManager: SubCategoryManager,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel(), ExpenseTrackerWidgetVM {
    private val subCategoryType = MutableStateFlow(Category.EXPENSE)

    private var amount = 0
    private var description: String = ""
    private var date = Date()
    private var categoryTag = ""
    private var category = Category.EXPENSE

    override fun setTransactionCategory(category: Category) {
        this.category = category
        subCategoryType.value = category
    }

    override fun setDate(date: Date) {
        this.date = date
    }

    override fun setDescription(description: String) {
        this.description = description
    }

    override fun setAmount(amount: Int) {
        this.amount = amount
    }

    override fun setCategoryTag(type: String) {
        this.categoryTag = type
    }

    override fun getTagList(): StateFlow<List<SubCategoryItem>> {
        if (sharedPrefManager.isNewUser()) {
            viewModelScope.launch(Dispatchers.IO) {
                subCategoryManager.addStoredSubCategoriesToDB()
            }
        }

        return combine(
            subCategoryType,
            subCategoryManager.getExpenseSubCategoriesFlow(),
            subCategoryManager.getIncomeSubCategoriesFlow()
        ) { category, expenseSubCategory, incomeSubCategory ->
            if (category == Category.EXPENSE)
                expenseSubCategory
            else
                incomeSubCategory
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    }

    override fun addUserTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            transactionManager.addTransaction(
                UserTransaction(
                    System.currentTimeMillis(),
                    amount, description, date, category, categoryTag
                )
            )
        }
    }
}
