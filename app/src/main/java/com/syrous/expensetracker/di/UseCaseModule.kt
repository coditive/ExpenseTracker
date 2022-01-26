package com.syrous.expensetracker.di

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.usecase.*
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object UseCaseModule {

    @Provides
    fun provideCreateSheetsUseCase(
        sharedPrefManager: SharedPrefManager,
        sheetApiRequest: SheetApiRequest
    ): CreateSheetsUseCase = CreateSheetsUseCase(sharedPrefManager, sheetApiRequest)

    @Provides
    fun provideModifySheetToTemplateUseCase(
        sharedPrefManager: SharedPrefManager,
        sheetApiRequest: SheetApiRequest
    ): ModifySheetToTemplateUseCase = ModifySheetToTemplateUseCase(
        sharedPrefManager, sheetApiRequest
    )

    @Provides
    fun provideSearchOrCreateAppFolderUseCase(
        driveApiRequest: DriveApiRequest,
        sharedPrefManager: SharedPrefManager
    ): SearchOrCreateAppFolderUseCase =
        SearchOrCreateAppFolderUseCase(driveApiRequest, sharedPrefManager)

    @Provides
    fun provideUploadUserTransactionUseCase(
        transactionManager: TransactionManager,
        driveApiRequest: DriveApiRequest,
        sharedPrefManager: SharedPrefManager
    ): UploadUserTransactionUseCase = UploadUserTransactionUseCase(
        transactionManager, driveApiRequest, sharedPrefManager
    )

    @Provides
    fun provideAppendTransactionsUseCase(
        transactionManager: TransactionManager,
        sheetApiRequest: SheetApiRequest,
        sharedPrefManager: SharedPrefManager
    ): AppendTransactionsUseCase = AppendTransactionsUseCase(
        transactionManager, sheetApiRequest, sharedPrefManager
    )

}