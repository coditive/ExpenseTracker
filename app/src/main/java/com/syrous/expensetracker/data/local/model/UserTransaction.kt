package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class UserTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val description: String,
    val date: Date,
    val transactionCategory: TransactionCategory,
    val categoryTag: String,
)