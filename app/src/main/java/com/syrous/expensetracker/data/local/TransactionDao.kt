package com.syrous.expensetracker.data.local

import androidx.room.*
import com.syrous.expensetracker.data.local.model.DBTransaction
import com.syrous.expensetracker.model.UserTransaction
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTransaction(transaction: DBTransaction)

    @Query("SELECT * from dbtransaction where timestamp = :timestamp")
    suspend fun getUserTransaction(timestamp: Long): DBTransaction

    @Query("SELECT * FROM dbtransaction ORDER BY date DESC")
    fun getAllUserTransactionsFlow(): Flow<List<DBTransaction>>

    @Query("SELECT * FROM dbtransaction")
    fun getAllUserTransactionsList(): List<DBTransaction>

    @Query("SELECT * FROM dbtransaction where isStoredOnSheet = 0")
    suspend fun getUnSyncedUserTransaction(): List<DBTransaction>

    @Query("UPDATE dbtransaction SET isStoredOnSheet = 1 WHERE timestamp = :timestamp")
    suspend fun updateUserTransactionSyncStatus(timestamp: Long)

    @Query("SELECT SUM(amount) from dbtransaction WHERE category = 'EXPENSE'")
    fun getTotalExpense(): Flow<Int>

    @Query("SELECT SUM(amount) from dbtransaction WHERE category = 'INCOME'")
    fun getTotalIncome(): Flow<Int>

    @Query("SELECT SUM(amount) from dbtransaction WHERE subCategoryId = :categoryId")
    suspend fun getTotalSpentAmountForCategoryTag(categoryId: Int): Int?

    @Query("SELECT * FROM dbtransaction WHERE subCategoryId = :categoryId")
    fun getUserTransactionsForCategoryTag(categoryId: Int): Flow<List<DBTransaction>>
}