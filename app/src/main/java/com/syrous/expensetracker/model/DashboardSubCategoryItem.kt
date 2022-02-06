package com.syrous.expensetracker.model

import androidx.annotation.DrawableRes

data class DashboardSubCategoryItem(
    val itemId: Int,
    val itemName: String,
    val amountSpent: Int = 0
)