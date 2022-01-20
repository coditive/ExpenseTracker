package com.syrous.expensetracker.utils

import com.syrous.expensetracker.BuildConfig

object Constants {
    val SharedPrefName = "com.syrous.expensetracker"
    val monthStartBalance = "month_start_balance"

    val userToken = "user_token"

    val emptyString = ""

    val apiKey = BuildConfig.API_KEY

    val webClientId = BuildConfig.WEB_CLIENT_ID

    val androidClientId = BuildConfig.ANDROID_CLIENT_ID

    val androidClientSecret = BuildConfig.ANDROID_CLIENT_SECRET

    val spreadSheetId = "spreadsheet_id"

    val gridSheetType = "GRID"

    val DATABASE_NAME = "expense_db"

    val DEFAULT_SHEET_NAME = "Sheet"

    val NEW_USER = "new_user"

}