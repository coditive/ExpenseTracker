package com.syrous.expensetracker.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


interface ExpenseTrackerWidgetVM {

    val viewState: StateFlow<BottomSheetViewState>

    fun setTransactionCategory(category: Category)

    fun setDate(date: Date)

    fun setDescription(description: String)

    fun setAmount(amount: Int)

    fun setCategoryTag(type: String)

    fun getTagList(): StateFlow<List<String>>

    fun addUserTransaction()
}


@HiltViewModel
class ExpenseTrackerWidgetVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val categoryManager: CategoryManager,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel(), ExpenseTrackerWidgetVM {

    private val _viewState = MutableStateFlow<BottomSheetViewState>(Success.defaultState())
    override val viewState: StateFlow<BottomSheetViewState>
        get() = _viewState

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

    override fun getTagList(): StateFlow<List<String>> {
        if (sharedPrefManager.isNewUser()) {
            viewModelScope.launch(Dispatchers.IO) {
                categoryManager.addStoredSubCategoriesToDB()
            }
        }

        return combine(
            subCategoryType,
            categoryManager.getExpenseSubCategoriesFlow(),
            categoryManager.getIncomeSubCategoriesFlow()
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

sealed class BottomSheetViewState


data class Success(
    val transactionCategory: Category,
    val date: Date,
    val categoryTag: String?,
    val description: String?,
    val amount: Int
    ): BottomSheetViewState() {
        companion object {
            fun defaultState(): Success {
                return Success(
                    Category.EXPENSE,
                    Date(),
                    null,
                    null,
                    0
                )
            }
        }
    }

data class Error(val element: String, val message: String): BottomSheetViewState()