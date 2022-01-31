package com.syrous.expensetracker.model

import androidx.annotation.DrawableRes

data class DashboardCategoryItem(
    val itemName: String,
    val amountSpent: Int,
    val count: Int
)