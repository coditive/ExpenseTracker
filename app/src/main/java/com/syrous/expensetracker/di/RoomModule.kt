package com.syrous.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.google.android.material.tabs.TabLayout
import com.syrous.expensetracker.data.local.ExpenseDB
import com.syrous.expensetracker.data.local.TransactionDao
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

    }

}