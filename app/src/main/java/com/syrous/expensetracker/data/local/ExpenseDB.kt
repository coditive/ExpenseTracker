package com.syrous.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syrous.expensetracker.data.local.converters.DateConverter
import com.syrous.expensetracker.data.local.model.UserTransaction


@TypeConverters(DateConverter::class)
@Database(
    entities = [
        UserTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDB: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}