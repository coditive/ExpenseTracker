package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.DBDashboardSubCategoryDao
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.utils.toDashboardCategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDashboardSubCategoryItemUseCase @Inject constructor(
    private val dashboardSubCategoryDao: DBDashboardSubCategoryDao,
    private val categoriesDao: CategoriesDao
) {

    fun execute(): Flow<List<DashboardCategoryItem>> =
        dashboardSubCategoryDao.getAllDBDashboardItems()
            .map { dbDashboardSubCategoryList ->
                dbDashboardSubCategoryList.map {
                    val categoryTag = categoriesDao.getSubCategoryFromId(it.subCategoryId)
                    it.toDashboardCategoryItem(categoryTag.name)
                }
            }
    

}