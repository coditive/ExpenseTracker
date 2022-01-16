package com.syrous.expensetracker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefManager constructor(sharedPreferences: SharedPreferences) {

    private var sharedPref: SharedPreferences = sharedPreferences

    fun addMonthStartBalance(amount: Int) {
        sharedPref.edit()
            .putInt(Constants.monthStartBalance, amount)
            .apply()
    }


    fun storeUserToken(token: String) {
        sharedPref.edit()
            .putString(Constants.userToken, "Bearer $token")
            .apply()
    }

    fun getUserToken(): String = sharedPref.getString(Constants.userToken, Constants.emptyString).toString()

    fun isNewUser(): Boolean = sharedPref.getBoolean(Constants.NEW_USER, true)

    fun makeUserRegular() {
        sharedPref.edit()
            .putBoolean(Constants.NEW_USER, false)
            .apply()
    }

    fun getMonthStartBalance(): Int {
      return  sharedPref.getInt(Constants.monthStartBalance, 0)
    }

}