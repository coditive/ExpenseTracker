package com.syrous.expensetracker.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.syrous.expensetracker.BuildConfig
import com.syrous.expensetracker.usecase.*
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit


@HiltWorker
class SpreadSheetSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val getAndUpdateSheetsUseCase: GetAndUpdateSheetsUseCase,
    private val searchOrCreateAppFolderUseCase: SearchOrCreateAppFolderUseCase,
    private val uploadUserTransactionUseCase: UploadUserTransactionUseCase,
    private val modifySheetToTemplateUseCase: ModifySheetToTemplateUseCase,
    private val appendTransactionsUseCase: AppendTransactionsUseCase,
    private val searchSheetUseCase: SearchSheetUseCase,
    private val sharedPrefManager: SharedPrefManager
) : CoroutineWorker(context, workParams) {
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun doWork(): Result {
        return if (!sharedPrefManager.isFileUploadedStatus()) {
            val searchUseCaseResult = searchOrCreateAppFolderUseCase.execute()
            if (searchUseCaseResult is Success) {
                val searchSpreadSheetUseCaseResult = searchSheetUseCase.execute()
                if(searchSpreadSheetUseCaseResult is Success) {
                    if(!sharedPrefManager.isFileUploadedStatus()) {
                        val uploadUseCaseResult =
                            uploadUserTransactionUseCase.uploadUserTransactionToDrive(
                                context,
                                Constants.spreadsheetFileName,
                                Constants.spreadsheetFileDescription
                            )
                        if (uploadUseCaseResult is Success) {
                            val createUseCaseResult = getAndUpdateSheetsUseCase.execute()
                            if (createUseCaseResult is Success) {
                                val modifyUseCaseResult = modifySheetToTemplateUseCase.execute(context)
                                if (modifyUseCaseResult is Success) {
                                    sharedPrefManager.storeFileUploadedStatus(true)
                                    Result.success()
                                } else {
                                    Log.d("SpreadsheetSyncWorker", (modifyUseCaseResult as Failure).message)
                                    Result.failure()
                                }
                            } else {
                                Log.d("SpreadsheetSyncWorker", (createUseCaseResult as Failure).message)
                                Result.failure()
                            }
                        } else {
                            Log.d("SpreadsheetSyncWorker", (uploadUseCaseResult as Failure).message)
                            Result.failure()
                        }
                    } else {
                        val appendUseCaseResult = appendTransactionsUseCase.execute()
                        if (appendUseCaseResult is Success)
                            Result.success()
                        else {
                            Log.d("SpreadsheetSyncWorker", (appendUseCaseResult as Failure).message)
                            Result.failure()
                        }
                    }
                } else {
                    Log.d("SpreadsheetSyncWorker", (searchSpreadSheetUseCaseResult as Failure).message)
                    Result.failure()
                }
            } else {
                Log.d("SpreadsheetSyncWorker", (searchUseCaseResult as Failure).message)
                Result.failure()
            }
        } else {
            val searchUseCaseResult = searchOrCreateAppFolderUseCase.execute()
            if (searchUseCaseResult is Success) {
                val searchSpreadSheetUseCaseResult = searchSheetUseCase.execute()
                if (searchSpreadSheetUseCaseResult is Success) {
                    val appendUseCaseResult = appendTransactionsUseCase.execute()
                    if (appendUseCaseResult is Success)
                        Result.success()
                    else {
                        Log.d("SpreadsheetSyncWorker", (appendUseCaseResult as Failure).message)
                        Result.failure()
                    }
                } else {
                    Log.d("SpreadsheetSyncWorker", (searchSpreadSheetUseCaseResult as Failure).message)
                    Result.failure()
                }
            } else {
                Log.d("SpreadsheetSyncWorker", (searchUseCaseResult as Failure).message)
                Result.failure()
            }
        }
    }

    companion object {
        const val SPREADSHEET_SYNC_WORKER_TAG = "spreadsheet_sync_worker_tag"
    }
}


fun WorkManager.enqueueSpreadSheetSyncWork() {
    if(BuildConfig.DEBUG) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)

        val workRequest = OneTimeWorkRequestBuilder<SpreadSheetSyncWorker>()
            .addTag(SpreadSheetSyncWorker.SPREADSHEET_SYNC_WORKER_TAG)
            .setConstraints(constraints.build())
            .build()

        this.enqueueUniqueWork(
            SpreadSheetSyncWorker.SPREADSHEET_SYNC_WORKER_TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    } else {
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
}