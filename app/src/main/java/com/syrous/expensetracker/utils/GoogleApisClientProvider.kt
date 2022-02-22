package com.syrous.expensetracker.utils

import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.AuthToken

interface GoogleApisClientProvider {

    var authToken: AuthToken?

    suspend fun driveApiClient(): DriveApi

    suspend fun sheetApiClient(): SheetApi
}