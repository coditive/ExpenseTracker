package com.syrous.expensetracker.data.remote.model

data class SheetProperties(
    val sheetId: Int,
    val title: String,
    val index: Int,
    val sheetType: String,
    val gridProperties: GridProperties? = null
)