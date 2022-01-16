package com.syrous.expensetracker.data.remote.model

data class SearchFileQueryResponse (
    val kind: String,
    val incompleteSearch: Boolean,
    val files: List<BasicFileMetaData>
    )