package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syrous.expensetracker.model.Category


@Entity
data class SubCategory(
    @PrimaryKey val id: Int,
    val name: String,
    val category: Category,
    val isStoredOnSheet: Boolean
)

