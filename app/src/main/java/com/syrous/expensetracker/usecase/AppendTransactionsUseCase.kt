package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.data.remote.model.ValuesRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import java.text.SimpleDateFormat
import java.util.*

class AppendTransactionsUseCase constructor(
    private val transactionManager: TransactionManager,
    private val sheetApiRequest: SheetApiRequest,
    private val sharedPrefManager: SharedPrefManager
) {

    private val sdf = SimpleDateFormat(Constants.datePattern, Locale.getDefault())
    suspend fun execute() {
        transactionManager
            .getUnSyncedTransaction()
            .collect { userTransactions ->

                val updatesList = mutableListOf<List<String>>()
                for (transaction in userTransactions) {
                    val record = mutableListOf<String>()
                    record.add(transaction.id.toString())
                    record.add(sdf.format(transaction.date))
                    record.add(transaction.description)
                    record.add(transaction.amount.toString())
                    record.add(transaction.category.name)
                    record.add(transaction.categoryTag)

                    updatesList.add(record)
                }

                sheetApiRequest.appendValueIntoSheet(
                    sharedPrefManager.getUserToken(),
                    sharedPrefManager.getSpreadSheetId(),
                    "Transactions!A:F",
                    Constants.apiKey,
                    "OVERWRITE",
                    null,
                    "FORMATTED_VALUE",
                    "USER_ENTERED",
                    ValuesRequest(updatesList)
                )

                transactionManager.updateTransactionListSyncStatus(userTransactions)
            }
    }
}