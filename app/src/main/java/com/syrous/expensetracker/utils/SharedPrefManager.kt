package com.syrous.expensetracker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefManager constructor(sharedPreferences: SharedPreferences) {

    private var sharedPref: SharedPreferences = sharedPreferences

    fun storeExpenseTrackerFolderId(id: String) {
        sharedPref.edit()
            .putString(Constants.expenseTrackerFolder, id)
            .apply()
    }


    fun storeSummarySheetId(id: Int) {
        sharedPref.edit()
            .putInt(Constants.summarySheetId, id)
            .apply()
    }

    fun storeTransactionSheetId(id: Int) {
        sharedPref.edit()
            .putInt(Constants.transactionSheetId, id)
            .apply()
    }

    fun storeCategoriesSheetId(id: Int) {
        sharedPref.edit()
            .putInt(Constants.categorySheetId, id)
            .apply()
    }

    fun storeFileUploadedStatus(status: Boolean) {
        sharedPref.edit()
            .putBoolean(Constants.fileUpload, status)
            .apply()
    }

    fun isFileUploadedStatus(): Boolean = sharedPref.getBoolean(Constants.fileUpload, false)

    fun getExpenseTrackerFolderId(): String =
        sharedPref.getString(Constants.expenseTrackerFolder, "").toString()

    fun getSummarySheetId(): Int = sharedPref.getInt(Constants.summarySheetId, 0)

    fun getTransactionSheetId(): Int = sharedPref.getInt(Constants.transactionSheetId, 0)

    fun getCategoriesSheetId(): Int = sharedPref.getInt(Constants.categorySheetId, 0)

    fun isNewUser(): Boolean = sharedPref.getBoolean(Constants.NEW_USER, true)

    fun makeUserRegular() {
        sharedPref.edit()
            .putBoolean(Constants.NEW_USER, false)
            .apply()
    }

    fun storeSpreadSheetId(spreadSheetId: String) {
        sharedPref.edit()
            .putString(Constants.spreadSheetId, spreadSheetId)
            .apply()
    }

    fun getSpreadSheetId(): String = sharedPref
        .getString(Constants.spreadSheetId, Constants.emptyString)
        .toString()

    fun getRefreshToken() = sharedPref.getString(Constants.refreshToken, Constants.emptyString).toString()

    fun storeRefreshToken(token: String) {
        sharedPref.edit()
            .putString(Constants.refreshToken, token)
            .apply()
    }
}