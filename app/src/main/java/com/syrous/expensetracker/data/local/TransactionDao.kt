package com.syrous.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

}