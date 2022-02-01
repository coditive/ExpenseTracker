package com.syrous.expensetracker.model

import androidx.annotation.DrawableRes

data class DashboardCategoryItem(
    val itemId: Int,
    val itemName: String,
    val amountSpent: Int = 0,
    val count: Int
)