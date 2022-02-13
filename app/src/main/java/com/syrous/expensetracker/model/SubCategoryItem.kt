package com.syrous.expensetracker.model

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class SubCategoryItem(
    val itemId: Int,
    val itemName: String,
    val amountSpent: Int = 0,
   @RawRes val animRes: Int? = null
)