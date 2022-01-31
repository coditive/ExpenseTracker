package com.syrous.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.syrous.expensetracker.data.local.model.DBDashboardSubCategoryItem
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.local.model.SubCategory
import kotlinx.coroutines.flow.Flow


@Dao
interface DBDashboardSubCategoryDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDBDashboardSubCategory(item: DBDashboardSubCategoryItem)

    @Query("SELECT * FROM dbdashboardsubcategoryitem")
    fun getAllDBDashboardItems(): Flow<List<DBDashboardSubCategoryItem>>

    @Query("SELECT * FROM subcategory " +
            "JOIN dbtransaction ON id = categoryId")
    fun getSubCategoryAndTransactionListMap(): Map<SubCategory, List<DBTransaction>>
}