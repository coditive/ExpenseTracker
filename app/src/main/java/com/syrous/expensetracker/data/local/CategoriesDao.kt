package com.syrous.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.local.model.SubCategory
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubCategory(subCategory: SubCategory)

    @Query("SELECT * FROM subcategory")
    suspend fun getAllSubCategory(): List<SubCategory>

    @Query("SELECT * FROM subcategory")
    fun getAllSubCategoriesFlow(): Flow<List<SubCategory>>

    @Query("SELECT id FROM subcategory where name = :name")
    suspend fun getSubCategoryId(name: String): Int

    @Query("SELECT * FROM subcategory where id =:id")
    suspend fun getSubCategoryFromId(id: Int): SubCategory
}