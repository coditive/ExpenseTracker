package com.syrous.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.syrous.expensetracker.data.local.model.UserTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserTransaction(transaction: UserTransaction)

    @Query("SELECT * from usertransaction")
    fun getAllUserTransactionsFlow(): Flow<List<UserTransaction>>

    @Query("SELECT * FROM usertransaction")
    fun getAllUserTransactions(): List<UserTransaction>

}