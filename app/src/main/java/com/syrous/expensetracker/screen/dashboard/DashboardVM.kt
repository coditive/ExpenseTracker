package com.syrous.expensetracker.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.GetDashboardSubCategoryItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface DashboardVM {

    val topCategoriesList: StateFlow<List<DashboardCategoryItem>>

    val categorizedUserTransactionList: StateFlow<List<UserTransaction>>

    fun setCurrentCategoryItemPosition(position: Int)
}

@HiltViewModel
class DashboardVMImpl @Inject constructor(
    private val getDashboardSubCategoryItemUseCase: GetDashboardSubCategoryItemUseCase
) : ViewModel(), DashboardVM {

    override val topCategoriesList: StateFlow<List<DashboardCategoryItem>>
        get() = getDashboardSubCategoryItemUseCase.execute()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _categorizedUserTransactionList = MutableStateFlow(emptyList<UserTransaction>())
    override val categorizedUserTransactionList: StateFlow<List<UserTransaction>>
        get() = _categorizedUserTransactionList


    override fun setCurrentCategoryItemPosition(position: Int) {

    }


}