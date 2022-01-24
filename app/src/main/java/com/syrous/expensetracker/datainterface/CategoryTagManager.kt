package com.syrous.expensetracker.datainterface

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.abs
import kotlin.random.Random

interface CategoryManager {

    fun getExpenseSubCategoriesFlow(): Flow<List<String>>

    fun getIncomeSubCategoriesFlow(): Flow<List<String>>

    suspend fun addStoredSubCategoriesToDB()

}

class CategoryManagerImpl(
    private val context: Context,
    private val categoriesDao: CategoriesDao
) : CategoryManager {

    override fun getExpenseSubCategoriesFlow(): Flow<List<String>> =
        categoriesDao.getAllSubCategoriesFlow().mapLatest { subCategoryList ->
            val nameList = mutableListOf<String>()
            subCategoryList.filter {
                it.category == Category.EXPENSE
            }.forEach {
                nameList.add(it.name)
            }
            nameList
        }

    override fun getIncomeSubCategoriesFlow(): Flow<List<String>> =
        categoriesDao.getAllSubCategoriesFlow().mapLatest { subCategoryList ->
            val nameList = mutableListOf<String>()
            subCategoryList.filter {
                it.category == Category.INCOME
            }.forEach {
                nameList.add(it.name)
            }
            nameList
        }

    override suspend fun addStoredSubCategoriesToDB() {
        val arrayOfExpenseSub = context.resources.getStringArray(R.array.expense_categories)
        val arrayOfIncomeSub = context.resources.getStringArray(R.array.income_categories)

        for (item in arrayOfExpenseSub)
            categoriesDao.addSubCategory(
                SubCategory(
                    abs(Random.nextInt()),
                    item,
                    Category.EXPENSE,
                    false
                )
            )

        for (item in arrayOfIncomeSub)
            categoriesDao.addSubCategory(
                SubCategory(
                    abs(Random.nextInt()),
                    item,
                    Category.INCOME,
                    false
                )
            )

    }
}