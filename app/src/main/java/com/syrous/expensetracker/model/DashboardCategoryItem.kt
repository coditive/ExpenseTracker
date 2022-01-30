package com.syrous.expensetracker.model

import androidx.annotation.DrawableRes

data class DashboardCategoryItem(
    val itemName: String,
    @DrawableRes val itemImage: Int,
    val amountSpent: Int
)