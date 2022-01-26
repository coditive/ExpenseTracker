package com.syrous.expensetracker.utils

import com.syrous.expensetracker.BuildConfig
import java.util.*

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

    val summarySheetId = "summary_sheet_id"

    val categorySheetId = "category_sheet_id"

    val transactionSheetId = "transaction_sheet_id"

    val DATABASE_NAME = "expense_db"

    val DEFAULT_SHEET_NAME = "Sheet"

    val NEW_USER = "new_user"

    val datePattern = "dd/MM/yyyy"

    val expenseTrackerFolder = "expense_tracker_folder"

    val spreadSheetMimeType = "application/vnd.google-apps.spreadsheet"

    val folderMimeType = "application/vnd.google-apps.folder"

    val corpora = "user"

    val appName = "Expense-Tracker"

    val fileUpload = "file_upload"

}