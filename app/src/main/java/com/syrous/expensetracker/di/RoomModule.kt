package com.syrous.expensetracker.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.syrous.expensetracker.data.local.SubCategoriesDao
import com.syrous.expensetracker.data.local.ExpenseDB
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.datainterface.SubCategoryManager
import com.syrous.expensetracker.datainterface.SubCategoryManagerImpl
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
        fun provideCategoriesDao(db: ExpenseDB): SubCategoriesDao = db.categoriesDao()


        @Provides
        fun provideCategoryTagManager(
            @ApplicationContext context: Context,
            subCategoriesDao: SubCategoriesDao
        ): SubCategoryManager = SubCategoryManagerImpl(context, subCategoriesDao)

        @Provides
        fun provideTransactionManager(
            transactionDao: TransactionDao,
            subCategoriesDao: SubCategoriesDao
        ): TransactionManager = TransactionManagerImpl(transactionDao, subCategoriesDao)

        @Provides
        fun initSharedPrefManager(@ApplicationContext context: Context): SharedPrefManager {
            val encryptedSharedPreferences = EncryptedSharedPreferences.create(
                Constants.SharedPrefName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return SharedPrefManager(encryptedSharedPreferences)
        }
    }

}