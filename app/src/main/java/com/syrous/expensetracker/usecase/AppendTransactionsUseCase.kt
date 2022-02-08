package com.syrous.expensetracker.usecase

import android.util.Log
import com.google.android.gms.common.api.internal.ApiKey
import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.SpreadsheetAppendResponse
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.UseCaseResult.*
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AppendTransactionsUseCase @Inject constructor(
    private val transactionManager: TransactionManager,
    private val sheetApiRequest: SheetApiRequest,
    private val sharedPrefManager: SharedPrefManager,
    @Named("apiKey") private val apiKey: String
) {
    private val TAG = this::class.java.name
    private val sdf = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    private val range = "Transactions!A:F"
    suspend fun execute(): UseCaseResult<SpreadsheetAppendResponse> {
        val valueRequest = convertUserTransactionsToValueRequest(
            transactionManager.getUnSyncedTransaction()
        )
        return try {
            if (valueRequest.values.isNotEmpty()) {
                val result = sheetApiRequest.appendValueIntoSheet(
                    sharedPrefManager.getUserToken(),
                    sharedPrefManager.getSpreadSheetId(),
                    range,
                    apiKey,
                    Constants.overwrite,
                    null,
                    Constants.formattedValue,
                    Constants.userEntered,
                    valueRequest
                )

                if (result.isSuccessful) {
                    transactionManager.getUnSyncedTransaction().forEach { userTransaction ->
                        transactionManager.updateTransactionListSyncStatus(userTransaction)
                    }
//                    Log.i(TAG, "append Success!!!")
                    Success(result.body()!!)
                } else
                    Failure(result.message())

            } else
                SucceedNoResultRequired()

        } catch (e: Exception) {
            Failure(e.message!!)
        }
    }

    private fun convertUserTransactionsToValueRequest(unSyncedTransaction: List<UserTransaction>): ValuesRequest {
        val updatesList = mutableListOf<List<String>>()
        unSyncedTransaction
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
        return ValuesRequest(updatesList)
    }
}