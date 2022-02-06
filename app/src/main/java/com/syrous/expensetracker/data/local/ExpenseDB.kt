package com.syrous.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syrous.expensetracker.data.converters.DateConverter
import com.syrous.expensetracker.data.local.model.SubCategory
import com.syrous.expensetracker.data.local.model.DBTransaction


@TypeConverters(DateConverter::class)
@Database(
    entities = [
        DBTransaction::class,
        SubCategory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoriesDao(): SubCategoriesDao
}