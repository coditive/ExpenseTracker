package com.syrous.expensetracker.data.remote.model

data class SetDataValidationRequest(
    val range: GridRange,
    val rule: DataValidationRule
)
