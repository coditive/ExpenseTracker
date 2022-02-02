package com.syrous.expensetracker.screen.addusertransaction

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

interface UserTransactionVM {

    val viewState: StateFlow<ViewState>

    fun setTransactionCategory(category: Category)

    fun setDate(date: Date)

    fun setDescription(description: String)

    fun setAmount(amount: Int)

    fun setCategoryTag(type: String)

    fun getTagList(): StateFlow<List<String>>

    fun addUserTransaction()
}

@HiltViewModel
class UserTransactionVMImpl @Inject constructor(
    private val transactionManager: TransactionManager,
    private val categoryManager: CategoryManager,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel(), UserTransactionVM {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Success.defaultState())
    override val viewState: StateFlow<ViewState>
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
                sharedPrefManager.makeUserRegular()
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

sealed class ViewState {

    data class Success(
        val category: Category,
        val date: Date,
        val categoryTag: String,
        val description: String,
        val amount: Int,
    ) : ViewState() {
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

    data class Error(val element: String, val errorMessage: String) : ViewState()
}