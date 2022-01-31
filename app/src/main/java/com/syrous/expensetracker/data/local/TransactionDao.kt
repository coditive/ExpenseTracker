package com.syrous.expensetracker.data.local

import androidx.room.*
import com.syrous.expensetracker.data.local.model.DBTransaction
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTransaction(transaction: DBTransaction)

    @Query("SELECT * from dbtransaction where timestamp = :timestamp")
    suspend fun getUserTransaction(timestamp: Long): DBTransaction

    @Query("SELECT * from dbtransaction")
    fun getAllUserTransactionsFlow(): Flow<List<DBTransaction>>

    @Query("SELECT * FROM dbtransaction")
    fun getAllUserTransactions(): List<DBTransaction>

    @Query("SELECT distinct(categoryId) FROM dbtransaction")
    fun getAllUserCategoriesIdList(): List<Int>

    @Query("SELECT * FROM dbtransaction where isStoredOnSheet = 0")
    fun getUnSyncedUserTransaction(): Flow<List<DBTransaction>>

    @Query("UPDATE dbtransaction SET isStoredOnSheet = 1 WHERE timestamp = :timestamp")
    suspend fun updateUserTransactionSyncStatus(timestamp: Long)

    @Query("SELECT SUM(amount) from dbtransaction WHERE category = 1")
    fun getTotalExpense(): Flow<Int>

    @Query("SELECT SUM(amount) from dbtransaction WHERE category = 0")
    fun getTotalIncome(): Flow<Int>


    @Query("SELECT SUM(amount) from dbtransaction WHERE categoryId = :categoryId")
    suspend fun getTotalSpentAmountForCategoryTag(categoryId: Int): Int
}