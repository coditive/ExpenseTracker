package com.syrous.expensetracker.data.remote.model

data class GridRange(
    val sheetId: Int,
    val startRowIndex: Int? = null,
    val endRowIndex: Int? = null,
    val startColumnIndex: Int? = null,
    val endColumnIndex: Int? = null
)