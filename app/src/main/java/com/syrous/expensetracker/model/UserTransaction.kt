package com.syrous.expensetracker.model

import java.util.*


data class UserTransaction(
    val id: Long,
    val amount: Int,
    val description: String,
    val date: Date,
    val category: Category,
    val categoryTag: String
)

