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
import kotlinx.coroutines.flow.combine
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
    private val subCategoriesDao: SubCategoriesDao
) : SubCategoryManager {

    override fun getExpenseSubCategoriesFlow(): Flow<List<SubCategoryItem>> =
        subCategoriesDao.getSubCategoryIdWithAmountSpent()
            .combine(subCategoriesDao.getExpenseSubCategoriesFlow()) { subCategoryWithAmountList, subCategoryList ->
                val nameList = mutableListOf<SubCategoryItem>()
                subCategoryList.forEach { subCategory ->
                    if (subCategory != null) {
                        val amount = if (subCategoryWithAmountList.isEmpty()) 0
                        else {
                            subCategoryWithAmountList.find { subCategoryWithAmount ->
                                subCategoryWithAmount.id == subCategory.id
                            }?.amount ?: 0
                        }
                        val animRes = when (subCategory.name) {
                            "Food" -> R.drawable.food
                            "Gifts" -> R.drawable.gift
                            "Medical/Health" -> R.drawable.healthcare
                            "Home" -> R.drawable.home
                            "Personal" -> R.drawable.personal
                            "MF" -> R.drawable.fund
                            else -> null
                        }
                        nameList.add(subCategory.toSubCategoryItem(amount, animRes))
                    }
                }
                nameList
            }

    override fun getIncomeSubCategoriesFlow(): Flow<List<SubCategoryItem>> =
        subCategoriesDao.getSubCategoryIdWithAmountEarned()
            .combine(subCategoriesDao.getIncomeSubCategoriesFlow()) { subCategoryIdWithAmountEarnedList, subCategoryList ->
                val nameList = mutableListOf<SubCategoryItem>()
                subCategoryList.forEach { subCategory ->
                    if (subCategory != null) {
                        val amount = if (subCategoryIdWithAmountEarnedList.isEmpty()) 0
                        else {
                            subCategoryIdWithAmountEarnedList.find { subCategoryWithAmount ->
                                subCategoryWithAmount.id == subCategory.id
                            }?.amount ?: 0
                        }
                        val animRes = when (subCategory.name) {
                            "Savings" -> R.drawable.savings
                            "Paycheck" -> R.drawable.paycheque
                            "Bonus" -> R.drawable.bonus
                            "Interest" -> R.drawable.interest
                            "Other" -> R.drawable.other
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