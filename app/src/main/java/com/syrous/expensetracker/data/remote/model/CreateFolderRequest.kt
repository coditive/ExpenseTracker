package com.syrous.expensetracker.data.remote.model

data class CreateFolderRequest(
    val name: String,
    val mimeType: String
)