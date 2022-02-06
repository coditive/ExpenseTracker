package com.syrous.expensetracker.usecase

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.usecase.UseCaseResult.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AppendTransactionsUseCase @Inject constructor(
    private val transactionManager: TransactionManager,
    private val sheetApiRequest: SheetApiRequest,
    private val sharedPrefManager: SharedPrefManager
) {
    private val TAG = this::class.java.name
    private val sdf = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    suspend fun execute(): UseCaseResult {
        val updatesList = mutableListOf<List<String>>()
        transactionManager
            .getUnSyncedTransaction()
            .forEach { userTransaction ->
                val record = mutableListOf<String>()
                record.add(userTransaction.id.toString())
                record.add(sdf.format(userTransaction.date))
                record.add(userTransaction.description)
                record.add(userTransaction.amount.toString())
                record.add(userTransaction.category.name)
                record.add(userTransaction.categoryTag)
                updatesList.add(record)
            }

        return try {
            val result = sheetApiRequest.appendValueIntoSheet(
                sharedPrefManager.getUserToken(),
                sharedPrefManager.getSpreadSheetId(),
                "Transactions!A:F",
                Constants.apiKey,
                Constants.overwrite,
                null,
                Constants.formattedValue,
                Constants.userEntered,
                ValuesRequest(updatesList)
            )

            if (result.isSuccessful) {
                transactionManager.getUnSyncedTransaction().forEach { userTransaction ->
                    transactionManager.updateTransactionListSyncStatus(userTransaction)
                }
                Log.i(TAG, "append Success!!!")
                Success(true)
            } else {
                Failure(result.message())
            }
        } catch (e: Exception) {
            Failure(e.message!!)
        }
    }
}