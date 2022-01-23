package com.syrous.expensetracker.data.remote.model


data class UpdateValueRequest(
    val data: List<UploadValue>,
    val valueInputOption: String,
    val includeValueInResponse: Boolean,
    val responseValueRenderOption: String,
    val responseDateTimeRenderOption: String
)