package com.syrous.expensetracker.model

data class Transaction(
    val id: Int,
    val amount: Int,
    val date: String,
    val category: String,
    val description: String,
    val timestamp: Long
)