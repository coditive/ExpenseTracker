package com.syrous.expensetracker.data.remote.model

data class SpreadSheetBatchUpdateRequest(
    val requests: List<SpreadSheetUpdateRequest>
)