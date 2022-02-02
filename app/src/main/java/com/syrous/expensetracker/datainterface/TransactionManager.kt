package com.syrous.expensetracker.datainterface

import com.firebase.ui.auth.data.model.User
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
import java.util.*

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

    fun getTotalExpenseAmount(): Flow<Int>

    fun getTotalIncomeAmount(): Flow<Int>

    fun getSubCategorisedUserTransactions(subCategoryId: Int): Flow<List<UserTransaction>>

    fun getAllTransactionDates(): Flow<List<Date>>
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

    override fun getTotalExpenseAmount(): Flow<Int> = transactionDao.getTotalExpense()

    override fun getTotalIncomeAmount(): Flow<Int> = transactionDao.getTotalIncome()

    override fun getSubCategorisedUserTransactions(subCategoryId: Int): Flow<List<UserTransaction>> =
        transactionDao.getUserTransactionsForCategoryTag(subCategoryId).map { dbTransaction ->
            val userTransactionList = mutableListOf<UserTransaction>()
            for (transaction in dbTransaction) {
                val category = categoriesDao.getSubCategoryFromId(transaction.categoryId)
                userTransactionList.add(transaction.toUserTransaction(categoryTag = category.name))
            }
            userTransactionList
        }

    override fun getAllTransactionDates(): Flow<List<Date>> = transactionDao.getAllTransactionDate()
}