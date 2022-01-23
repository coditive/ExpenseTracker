package com.syrous.expensetracker.data.remote.model

data class SpreadSheetResponse(
    val spreadsheetId: String,
    val spreadsheetUrl: String,
    val sheets: List<GetSheetResponse>
)