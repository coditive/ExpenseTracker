package com.syrous.expensetracker.datainterface

import com.syrous.expensetracker.data.local.SubCategoriesDao
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

    suspend fun getAllTransactionListFromStorage(): List<UserTransaction>

    fun getCategorisedTransactionsFromStorage(
        category: Category
    ): Flow<List<UserTransaction>>

    suspend fun getUnSyncedTransaction(): List<UserTransaction>

    suspend fun updateTransactionListSyncStatus(userTransaction: UserTransaction)

    suspend fun syncUserTransaction(sheetName: String = Constants.DEFAULT_SHEET_NAME)

    suspend fun syncAndUploadUserTransactions()

    fun getTotalExpenseAmount(): Flow<Int>

    fun getTotalIncomeAmount(): Flow<Int>

    fun getSubCategorisedUserTransactions(subCategoryId: Int): Flow<List<UserTransaction>>

}

class TransactionManagerImpl(
    private val transactionDao: TransactionDao,
    private val subCategoriesDao: SubCategoriesDao
) : TransactionManager {

    override suspend fun addTransaction(transaction: UserTransaction) {
        val id = subCategoriesDao.getSubCategoryId(transaction.categoryTag)
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
                val category = subCategoriesDao.getSubCategoryFromId(transaction.subCategoryId)
                userTransactionList.add(transaction.toUserTransaction(categoryTag = category.name))
            }
            userTransactionList
        }

    override suspend fun getAllTransactionListFromStorage(): List<UserTransaction> = transactionDao
        .getAllUserTransactionsList()
        .map { userTransaction ->
            val subCategory = subCategoriesDao.getSubCategoryFromId(userTransaction.subCategoryId)
            userTransaction.toUserTransaction(subCategory.name)
        }


    override fun getCategorisedTransactionsFromStorage(
        category: Category
    ): Flow<List<UserTransaction>> = getAllTransactionsFromStorage().mapLatest { transactionList ->
        transactionList.filter { transaction ->
            transaction.category.value != category.value
        }
    }

    override suspend fun getUnSyncedTransaction(): List<UserTransaction> = transactionDao
        .getUnSyncedUserTransaction()
        .map { transaction ->
            val category = subCategoriesDao.getSubCategoryFromId(transaction.subCategoryId)
            transaction.toUserTransaction(categoryTag = category.name)
        }

    override suspend fun updateTransactionListSyncStatus(userTransaction: UserTransaction) {
        transactionDao.updateUserTransactionSyncStatus(userTransaction.id)
    }

    override fun getTotalExpenseAmount(): Flow<Int> = transactionDao.getTotalExpense()

    override fun getTotalIncomeAmount(): Flow<Int> = transactionDao.getTotalIncome()

    override fun getSubCategorisedUserTransactions(subCategoryId: Int): Flow<List<UserTransaction>> =
        transactionDao.getUserTransactionsForCategoryTag(subCategoryId).map { dbTransaction ->
            val userTransactionList = mutableListOf<UserTransaction>()
            dbTransaction.forEach { transaction ->
                val category = subCategoriesDao.getSubCategoryFromId(transaction.subCategoryId)
                userTransactionList.add(transaction.toUserTransaction(categoryTag = category.name))
            }
            userTransactionList
        }

}