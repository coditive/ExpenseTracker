package com.syrous.expensetracker.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.usecase.*
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltWorker
class SpreadSheetSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val createSheetsUseCase: CreateSheetsUseCase,
    private val searchOrCreateAppFolderUseCase: SearchOrCreateAppFolderUseCase,
    private val uploadUserTransactionUseCase: UploadUserTransactionUseCase,
    private val modifySheetToTemplateUseCase: ModifySheetToTemplateUseCase,
    private val appendTransactionsUseCase: AppendTransactionsUseCase,
    private val sharedPrefManager: SharedPrefManager
) : CoroutineWorker(context, workParams) {
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun doWork(): Result =
        try {
            if (sharedPrefManager.isFileUploadStatus()) {
                searchOrCreateAppFolderUseCase.execute()
                delay(100)
                uploadUserTransactionUseCase.uploadUserTransactionToDrive(
                    context,
                    "Total-Expense-Sheet.csv",
                    "All in one sheet for expenses"
                )
                delay(100)
                createSheetsUseCase.execute()
                delay(100)
                modifySheetToTemplateUseCase.execute(context)
                sharedPrefManager.storeFileUploadStatus(true)
            } else {
                appendTransactionsUseCase.execute()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

    companion object {
        const val SPREADSHEET_SYNC_WORKER_TAG = "spreadsheet_sync_worker_tag"
    }
}


fun WorkManager.enqueueSpreadSheetSyncWork() {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) constraints.setRequiresDeviceIdle(true)

    val workRequest = PeriodicWorkRequestBuilder<SpreadSheetSyncWorker>(1, TimeUnit.DAYS)
        .addTag(SpreadSheetSyncWorker.SPREADSHEET_SYNC_WORKER_TAG)
        .setConstraints(constraints.build())
        .build()
    enqueueUniquePeriodicWork(
        SpreadSheetSyncWorker.SPREADSHEET_SYNC_WORKER_TAG,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}