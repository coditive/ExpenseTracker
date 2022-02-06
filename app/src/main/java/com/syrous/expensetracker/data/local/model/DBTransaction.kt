package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.syrous.expensetracker.model.Category
import java.util.*


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = SubCategory::class,
            parentColumns = ["id"],
            childColumns = ["subCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DBTransaction(
    @PrimaryKey val timestamp: Long,
    val amount: Int,
    val description: String,
    val category: Category,
    val date: Date,
    val subCategoryId: Int,
    val isStoredOnSheet: Boolean
)
