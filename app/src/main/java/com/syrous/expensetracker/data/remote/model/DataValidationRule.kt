package com.syrous.expensetracker.data.remote.model

data class DataValidationRule(
    val condition: BooleanCondition,
    val inputMessage: String? = null,
    val strict: Boolean,
    val showCustomUi: Boolean
)
