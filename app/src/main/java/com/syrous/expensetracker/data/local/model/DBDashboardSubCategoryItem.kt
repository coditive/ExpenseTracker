package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class DBDashboardSubCategoryItem(
    @PrimaryKey val subCategoryId: Int,
    val count: Int,
    val totalAmountSpent: Int,
    val lastModified: Long
)