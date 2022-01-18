package com.syrous.expensetracker.datainterface

import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.TransactionCategory.EXPENSE
import com.syrous.expensetracker.data.local.model.TransactionCategory.INCOME
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.ApiRequest
import com.syrous.expensetracker.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface TransactionManager {

    fun addTransaction(transaction: UserTransaction)

    fun getAllTransactionsFromStorage(): Flow<List<UserTransaction>>

    fun getCategorisedTransactionsFromStorage(transactionCategory: TransactionCategory): Flow<List<UserTransaction>>

    suspend fun syncUserTransaction(sheetName: String = Constants.DEFAULT_SHEET_NAME)

    suspend fun syncAndUploadUserTransactions()

    suspend fun getTotalExpenses(): Int

    suspend fun getTotalIncome(): Int
}

class TransactionManagerImpl(
    private val transactionDao: TransactionDao,
    private val coroutineScope: CoroutineScope
) : TransactionManager {

    override fun addTransaction(transaction: UserTransaction) {
        coroutineScope.launch {
            transactionDao.insertUserTransaction(transaction)
            transactionDao.getUserTransaction(transaction.date, transaction.description)
        }
    }

    override suspend fun syncUserTransaction(sheetName: String) {

    }

    override suspend fun syncAndUploadUserTransactions() {

    }


    override fun getAllTransactionsFromStorage(): Flow<List<UserTransaction>> =
        transactionDao.getAllUserTransactionsFlow()

    override fun getCategorisedTransactionsFromStorage(transactionCategory: TransactionCategory): Flow<List<UserTransaction>> =
        transactionDao.getAllUserTransactionsFlow().map { userTransactionList ->
            userTransactionList.filter { transaction ->
                transaction.transactionCategory == transactionCategory
            }
        }

    override suspend fun getTotalExpenses(): Int {
        var expense = 0
        transactionDao.getAllUserTransactions()
            .filter { it.transactionCategory == EXPENSE }
            .forEach {
                expense += it.amount
            }
        return expense
    }

    override suspend fun getTotalIncome(): Int {
        var income = 0
        transactionDao.getAllUserTransactions()
            .filter { it.transactionCategory == INCOME }
            .forEach {
                income += it.amount
            }
        return income
    }
}