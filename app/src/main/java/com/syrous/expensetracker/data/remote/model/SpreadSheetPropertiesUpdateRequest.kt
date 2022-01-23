package com.syrous.expensetracker.data.remote.model

data class SpreadSheetPropertiesUpdateRequest(
    val properties: SheetProperties,
    val fields: String
)