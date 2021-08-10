package com.syrous.expensetracker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefManager private constructor(sharedPreferences: SharedPreferences) {

    private var sharedPref: SharedPreferences = sharedPreferences

    fun addMonthStartBalance(amount: Int) {
        sharedPref.edit()
            .putInt(Constants.monthStartBalance, amount)
            .apply()
    }

    companion object {
        fun initSharedPrefManager(context: Context): SharedPrefManager {
            val sharedPref = context.getSharedPreferences(Constants.SharedPrefName, MODE_PRIVATE)
            return SharedPrefManager(sharedPref)
        }
    }
}