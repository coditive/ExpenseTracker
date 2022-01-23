package com.syrous.expensetracker.data.remote.model

data class BooleanCondition (
    val type: String,
    val values: List<ConditionValue>
    )