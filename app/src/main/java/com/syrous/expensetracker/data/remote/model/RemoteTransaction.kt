package com.syrous.expensetracker.data.remote.model

import com.syrous.expensetracker.data.local.model.UserTransaction


data class RemoteTransaction(
    val rows: List<UserTransaction>
)