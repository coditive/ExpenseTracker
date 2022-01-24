package com.syrous.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.ExpenseDB
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.CategoryManagerImpl
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.datainterface.TransactionManagerImpl
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
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
                Constants.DATABASE_NAME
            ).build()
            return expenseDB
        }

        @Provides
        fun provideTransactionDao(db: ExpenseDB): TransactionDao = db.transactionDao()

        @Provides
        fun provideCategoriesDao(db: ExpenseDB): CategoriesDao = db.categoriesDao()

        @Provides
        fun provideCategoryTagManager(
            @ApplicationContext context: Context,
            categoriesDao: CategoriesDao
        ): CategoryManager = CategoryManagerImpl(context, categoriesDao)

        @Provides
        fun provideTransactionManager(
            transactionDao: TransactionDao,
            categoriesDao: CategoriesDao
        ): TransactionManager = TransactionManagerImpl(transactionDao, categoriesDao)

        @Provides
        fun initSharedPrefManager(@ApplicationContext context: Context): SharedPrefManager {
            val sharedPref = context.getSharedPreferences(
                Constants.SharedPrefName,
                Context.MODE_PRIVATE
            )
            return SharedPrefManager(sharedPref)
        }
    }

}