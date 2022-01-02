package com.syrous.expensetracker.data.local.model

data class Category(
    val id: Int,
    val name: String
    )


enum class TransactionCategory(val value: Int) {
    INCOME (0),
    EXPENSE(1)
}