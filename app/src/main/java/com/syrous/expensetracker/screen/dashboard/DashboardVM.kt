package com.syrous.expensetracker.screen.dashboard

import androidx.lifecycle.ViewModel
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DashboardVM {

    val topCategoriesList: StateFlow<List<DashboardCategoryItem>>

    val categorizedUserTransactionList: StateFlow<List<UserTransaction>>

    fun setCurrentCategoryItemPosition(position: Int)
}

class DashboardVMImpl: ViewModel(), DashboardVM {

    private val _topCategoriesList = MutableStateFlow(emptyList<DashboardCategoryItem>())
    override val topCategoriesList: StateFlow<List<DashboardCategoryItem>>
        get() = _topCategoriesList

    private val _categorizedUserTransactionList = MutableStateFlow(emptyList<UserTransaction>())
    override val categorizedUserTransactionList: StateFlow<List<UserTransaction>>
        get() = _categorizedUserTransactionList


    override fun setCurrentCategoryItemPosition(position: Int) {

    }


}