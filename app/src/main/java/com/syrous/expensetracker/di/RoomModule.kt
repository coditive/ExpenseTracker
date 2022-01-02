package com.syrous.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.syrous.expensetracker.data.local.ExpenseDB
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.CategoryManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class RoomModule {

    companion object {
        private lateinit var expenseDB: ExpenseDB

        @Singleton
        @Provides
        fun provideDB(@ApplicationContext context: Context): ExpenseDB {
            expenseDB = Room.databaseBuilder(
                context.applicationContext,
                ExpenseDB::class.java,
                "expense_DB"
            ).build()
            return expenseDB
        }

        @Provides
        fun provideTransactionDao(db: ExpenseDB): TransactionDao = db.transactionDao()

        @Provides
        fun provideCategoryTagManager(@ApplicationContext context: Context): CategoryManager {
            return CategoryManagerImpl(context)
        }

    }

}