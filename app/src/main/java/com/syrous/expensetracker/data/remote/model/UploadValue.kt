package com.syrous.expensetracker.data.remote.model

data class UploadValue(
    val range: String,
    val values: List<String>,
    val majorDimension: String
)