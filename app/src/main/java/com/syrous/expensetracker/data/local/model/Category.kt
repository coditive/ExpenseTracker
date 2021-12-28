package com.syrous.expensetracker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Category(
    val id: Int,
    val name: String
    )