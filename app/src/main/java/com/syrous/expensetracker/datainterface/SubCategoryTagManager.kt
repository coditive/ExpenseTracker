package com.syrous.expensetracker.datainterface

import android.content.Context
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.local.SubCategoriesDao
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.utils.toSubCategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.abs
import kotlin.random.Random

interface SubCategoryManager {

    fun getExpenseSubCategoriesFlow(): Flow<List<SubCategoryItem>>

    fun getIncomeSubCategoriesFlow(): Flow<List<SubCategoryItem>>

    suspend fun addStoredSubCategoriesToDB()

}

class SubCategoryManagerImpl(
    private val context: Context,
    private val subCategoriesDao: SubCategoriesDao,
    private val transactionDao: TransactionDao
) : SubCategoryManager {

    override fun getExpenseSubCategoriesFlow(): Flow<List<SubCategoryItem>> =
        subCategoriesDao.getAllSubCategoriesFlow().mapLatest { subCategoryList ->
            val nameList = mutableListOf<SubCategoryItem>()
            subCategoryList.filter {
                it.category == Category.EXPENSE
            }.forEach { subCategory ->
                if(subCategory != null) {
                    val amount = transactionDao.getTotalSpentAmountForCategoryTag(subCategory.id) ?: 0
                    val animRes = when (subCategory.name) {
                        "Food" -> R.raw.food
                        "Gifts" -> R.raw.gift
                        "Medical/Health" -> R.raw.medical_syringe
                        "Home" -> R.raw.home_icon_loading
                        "Personal" -> R.raw.profile
                        "MF" -> R.raw.rupee_coin
                        else -> null
                    }
                    nameList.add(subCategory.toSubCategoryItem(amount, animRes))
                }
            }
            nameList
        }

    override fun getIncomeSubCategoriesFlow(): Flow<List<SubCategoryItem>> =
        subCategoriesDao.getAllSubCategoriesFlow().mapLatest { subCategoryList ->
            val nameList = mutableListOf<SubCategoryItem>()
            subCategoryList.filter {
                it.category == Category.INCOME
            }.forEach { subCategory ->
                if(subCategory != null) {
                    val amount = transactionDao.getTotalSpentAmountForCategoryTag(subCategory.id) ?: 0
                    val animRes = when (subCategory.name) {
                        "Savings" -> null
                        "Paycheck" -> null
                        "Bonus" -> null
                        "Interest" -> null
                        "Other" -> null
                        else -> null
                    }
                    nameList.add(subCategory.toSubCategoryItem(amount, animRes))
                }
            }
            nameList
        }

    override suspend fun addStoredSubCategoriesToDB() {
        val arrayOfExpenseSub = context.resources.getStringArray(R.array.expense_categories)
        val arrayOfIncomeSub = context.resources.getStringArray(R.array.income_categories)

        for (item in arrayOfExpenseSub)
            subCategoriesDao.addSubCategory(
                SubCategory(
                    abs(Random.nextInt()),
                    item,
                    Category.EXPENSE,
                    false
                )
            )

        for (item in arrayOfIncomeSub)
            subCategoriesDao.addSubCategory(
                SubCategory(
                    abs(Random.nextInt()),
                    item,
                    Category.INCOME,
                    false
                )
            )

    }
}