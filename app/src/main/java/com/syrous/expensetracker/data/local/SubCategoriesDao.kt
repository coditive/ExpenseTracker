package com.syrous.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.data.local.model.SubCategoryWithAmount
import kotlinx.coroutines.flow.Flow


@Dao
interface SubCategoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubCategory(subCategory: SubCategory)

    @Query("SELECT * FROM subcategory where category = 'EXPENSE'")
    fun getExpenseSubCategoriesFlow(): Flow<List<SubCategory>>

    @Query("SELECT * FROM subcategory where category = 'INCOME'")
    fun getIncomeSubCategoriesFlow(): Flow<List<SubCategory>>

    @Query("SELECT id FROM subcategory where name = :name")
    suspend fun getSubCategoryId(name: String): Int

    @Query("SELECT * FROM subcategory where id =:id")
    suspend fun getSubCategoryFromId(id: Int): SubCategory

    @Query("SELECT sum(amount) as amount, id FROM subcategory JOIN DBTransaction ON id = subCategoryId WHERE subCategory.category = 'EXPENSE' GROUP BY id")
    fun getSubCategoryIdWithAmountSpent(): Flow<List<SubCategoryWithAmount>>

    @Query("SELECT sum(amount) as amount, id FROM subcategory JOIN DBTransaction ON id = subCategoryId WHERE subCategory.category = 'INCOME' GROUP BY id ")
    fun getSubCategoryIdWithAmountEarned(): Flow<List<SubCategoryWithAmount>>
}