package com.syrous.expensetracker.screen.usertransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.datainterface.TransactionManagerImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject

interface UserTransactionVM {

    val viewState: StateFlow<ViewState>

    fun setTransactionCategory(transactionCategory: TransactionCategory)

    fun setDate(date: Date)

    fun setDescription(description: String)

    fun setAmount(amount: Int)

    fun setCategoryTag(type: String)

    fun getTagList(): List<String>

    fun addUserTransaction()
}

@HiltViewModel
class UserTransactionVMImpl @Inject constructor(
    transactionDao: TransactionDao,
    driveApiRequest: DriveApiRequest,
    private val categoryManager: CategoryManager
): ViewModel(), UserTransactionVM {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Success.defaultState())
    override val viewState: StateFlow<ViewState>
        get() = _viewState

    private val transactionManager: TransactionManager = TransactionManagerImpl(transactionDao,  viewModelScope)

    private var amount = 0
    private var description: String = ""
    private var date = Date()
    private var categoryTag = ""
    private var transactionCategory = TransactionCategory.EXPENSE

    override fun setTransactionCategory(transactionCategory: TransactionCategory) {
        this.transactionCategory = transactionCategory
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

    override fun getTagList(): List<String> {
       return if(transactionCategory == TransactionCategory.EXPENSE) {
            categoryManager.getStoredExpenseCategories().toList()
        } else {
            categoryManager.getStoredIncomeCategories().toList()
        }
    }

    override fun addUserTransaction() {
        val result = transactionValuesValidator(amount, transactionCategory, description, date, categoryTag)
        if(result != null) {
           transactionManager.addTransaction(result)
            _viewState.value = ViewState.Success(transactionCategory, date, categoryTag, description, amount, isTransactionAdded = true)
        }
    }

    private fun transactionValuesValidator(
        amount: Int,
        transactionCategory: TransactionCategory,
        description: String,
        date: Date,
        categoryTag: String
    ): UserTransaction? {

        if(categoryTag.isEmpty()) {
            _viewState.value = ViewState.Error("category_tag", "Type is Null")
            return null
        }

        if (description.isEmpty()) {
            _viewState.value = ViewState.Error("description", "Description is null")
            return null
        }

        _viewState.value = ViewState.Success(transactionCategory, date, categoryTag, description, amount, false)
        return UserTransaction(amount = amount, description = description, transactionCategory = transactionCategory, date = date, categoryTag = categoryTag)
    }

}

sealed class ViewState {

    data class Success(
        val transactionCategory: TransactionCategory,
        val date: Date,
        val categoryTag: String,
        val description: String,
        val amount: Int,
        val isTransactionAdded: Boolean
    ) : ViewState() {
        companion object {
            fun defaultState(): Success {
                return Success(
                    TransactionCategory.EXPENSE,
                    Date(),
                    categoryTag = "",
                    description = "",
                    amount = 0,
                    isTransactionAdded = false
                )
            }
        }
    }

    data class Error(val element: String, val errorMessage: String) : ViewState()
}