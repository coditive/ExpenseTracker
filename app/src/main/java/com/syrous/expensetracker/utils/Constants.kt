package com.syrous.expensetracker.utils

import com.syrous.expensetracker.BuildConfig
import java.util.*

object Constants {
    const val SharedPrefName = "com.syrous.expensetracker"

    const val monthStartBalance = "month_start_balance"

    const val userToken = "user_token"

    const val emptyString = ""

    const val apiKey = BuildConfig.API_KEY

    const val webClientId = BuildConfig.WEB_CLIENT_ID

    const val androidClientSecret = BuildConfig.ANDROID_CLIENT_SECRET

    const val spreadSheetId = "spreadsheet_id"

    const val gridSheetType = "GRID"

    const val summarySheetId = "summary_sheet_id"

    const val categorySheetId = "category_sheet_id"

    const val transactionSheetId = "transaction_sheet_id"

    const val DATABASE_NAME = "expense_db"

    const val DEFAULT_SHEET_NAME = "Sheet"

    const val NEW_USER = "new_user"

    const val datePattern = "dd/MM/yyyy"

    const val expenseTrackerFolder = "expense_tracker_folder"

    const val spreadSheetMimeType = "application/vnd.google-apps.spreadsheet"

    const val folderMimeType = "application/vnd.google-apps.folder"

    const val corpora = "user"

    const val appName = "Expense-Tracker"

    const val fileUpload = "file_upload"

    const val overwrite = "OVERWRITE"

    const val formattedValue = "FORMATTED_VALUE"

    const val userEntered = "USER_ENTERED"

    const val transaction = "Transactions"

    const val summary = "Summary"

    const val categories = "Categories"

    const val title = "title"

    const val formula = "FORMULA"

    const val unformattedValue = "UNFORMATTED_VALUE"

    const val spreadsheetFileName = "Total-Expense-Sheet.csv"

    const val spreadsheetFileDescription = "All in one sheet for expenses"

    const val rupeeSign = "₹"

    const val category = "Category"

    const val expense = "Expense"

    const val income = "Income"

    const val amount = "Amount"

    const val description = "Description"

    const val today = "Today"

    const val yesterday = "Yesterday"

    const val headerFormat = "dd MMMM"

    const val selectDate = "Select date"

    const val datePicker = "date-picker"

    const val blankSpace = " "

    const val serverAuthCode = "server-auth-code"

    const val refreshToken = "refresh_token"

    const val authorizationCode = "authorization_code"
}