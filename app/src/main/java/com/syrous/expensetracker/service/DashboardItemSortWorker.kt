package com.syrous.expensetracker.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.syrous.expensetracker.data.local.DBDashboardSubCategoryDao
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.DBDashboardSubCategoryItem
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.data.local.model.SubCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs
import kotlin.random.Random


@HiltWorker
class DashboardItemSortWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dashboardDao: DBDashboardSubCategoryDao,
    private val transactionDao: TransactionDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            dashboardDao.getSubCategoryAndTransactionListMap()
                .forEach { entry: Map.Entry<SubCategory, List<DBTransaction>> ->
                val totalAmountSpent =
                    transactionDao.getTotalSpentAmountForCategoryTag(entry.key.id)
                dashboardDao.insertDBDashboardSubCategory(
                    DBDashboardSubCategoryItem(
                        entry.key.id,
                        entry.value.size,
                        totalAmountSpent,
                        System.currentTimeMillis()
                    )
                )

            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val DASHBOARD_ITEM_SORT_WORKER_TAG = "dashboard_worker_tag"
    }
}


fun WorkManager.enqueueDashboardItemSortWorker() {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)

    val workRequest = OneTimeWorkRequestBuilder<DashboardItemSortWorker>()
        .addTag(DashboardItemSortWorker.DASHBOARD_ITEM_SORT_WORKER_TAG)
        .setConstraints(constraints.build())
        .build()

    this.enqueueUniqueWork(
        DashboardItemSortWorker.DASHBOARD_ITEM_SORT_WORKER_TAG,
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}