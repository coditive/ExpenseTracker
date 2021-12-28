package com.syrous.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syrous.expensetracker.data.local.converters.DateConverter
import com.syrous.expensetracker.data.local.model.Transaction


@TypeConverters(DateConverter::class)
@Database(
    entities = [
        Transaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDB: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}