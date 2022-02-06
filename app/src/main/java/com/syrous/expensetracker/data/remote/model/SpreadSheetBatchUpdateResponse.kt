package com.syrous.expensetracker.data.remote.model

data class SpreadSheetBatchUpdateResponse(
    val spreadsheetId: String,
    val replies: List<SpreadSheetUpdateRequest>
)