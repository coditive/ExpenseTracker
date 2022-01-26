package com.syrous.expensetracker.datainterface

import com.syrous.expensetracker.data.local.CategoriesDao
import com.syrous.expensetracker.data.local.TransactionDao
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.toDBTransaction
import com.syrous.expensetracker.utils.toUserTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

interface TransactionManager {

    suspend fun addTransaction(transaction: UserTransaction)

    fun getAllTransactionsFromStorage(): Flow<List<UserTransaction>>

    suspend fun getAllTransactions(): List<UserTransaction>

    fun getCategorisedTransactionsFromStorage(
        category: Category
    ): Flow<List<UserTransaction>>

    fun getUnSyncedTransaction(): Flow<List<UserTransaction>>

    suspend fun updateTransactionListSyncStatus(userTransactionList: List<UserTransaction>)

    suspend fun syncUserTransaction(sheetName: String = Constants.DEFAULT_SHEET_NAME)

    suspend fun syncAndUploadUserTransactions()

    suspend fun getTotalExpenses(): Int

    suspend fun getTotalIncome(): Int
}

class TransactionManagerImpl(
    private val transactionDao: TransactionDao,
    private val categoriesDao: CategoriesDao
) : TransactionManager {

    override suspend fun addTransaction(transaction: UserTransaction) {
        val id = categoriesDao.getSubCategoryId(transaction.categoryTag)
        transactionDao.insertUserTransaction(transaction.toDBTransaction(id))
    }

    override suspend fun syncUserTransaction(sheetName: String) {

    }

    override suspend fun syncAndUploadUserTransactions() {

    }

    override fun getAllTransactionsFromStorage(): Flow<List<UserTransaction>> =
        transactionDao.getAllUserTransactionsFlow().map { dbTransaction ->
            val userTransactionList = mutableListOf<UserTransaction>()
            for (transaction in dbTransaction) {
                val category = categoriesDao.getSubCategoryFromId(transaction.categoryId)
                userTransactionList.add(transaction.toUserTransaction(categoryTag = category.name))
            }
            userTransactionList
        }

    override suspend fun getAllTransactions(): List<UserTransaction> =
        transactionDao.getAllUserTransactions().map { dbTransaction ->
            val categoryTag = categoriesDao.getSubCategoryFromId(dbTransaction.categoryId)
            dbTransaction.toUserTransaction(categoryTag.name)
        }


    override fun getCategorisedTransactionsFromStorage(
        category: Category
    ): Flow<List<UserTransaction>> = getAllTransactionsFromStorage().mapLatest { transactionList ->
        transactionList.filter { transaction ->
            transaction.category.value != category.value
        }
    }

    override fun getUnSyncedTransaction(): Flow<List<UserTransaction>> = transactionDao
        .getUnSyncedUserTransaction()
        .map { dbTransaction ->
            val userTransactionList = mutableListOf<UserTransaction>()
            for (transaction in dbTransaction) {
                val category = categoriesDao.getSubCategoryFromId(transaction.categoryId)
                userTransactionList.add(transaction.toUserTransaction(categoryTag = category.name))
            }
            userTransactionList
        }

    override suspend fun updateTransactionListSyncStatus(userTransactionList: List<UserTransaction>) {
        for (transaction in userTransactionList) {
            transactionDao.updateUserTransactionSyncStatus(transaction.id)
        }
    }

    override suspend fun getTotalExpenses(): Int {
        var expense = 0
        transactionDao.getAllUserTransactions()
            .filter { it.category == Category.EXPENSE }
            .forEach {
                expense += it.amount
            }
        return expense
    }

    override suspend fun getTotalIncome(): Int {
        var income = 0
        transactionDao.getAllUserTransactions()
            .filter { it.category == Category.INCOME }
            .forEach {
                income += it.amount
            }
        return income
    }
}