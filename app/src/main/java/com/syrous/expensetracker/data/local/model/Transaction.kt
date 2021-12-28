package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Transaction(
    @PrimaryKey val id: Int,
    val amount: Int,
    val date: String,
    val category: Int,
    val description: String,
    val timestamp: Date
)