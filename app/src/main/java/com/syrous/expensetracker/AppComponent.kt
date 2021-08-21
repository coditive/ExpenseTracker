package com.syrous.expensetracker

import android.content.Context
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.screen.TransactionManagerImpl
import com.syrous.expensetracker.utils.SharedPrefManager

class AppComponent(private val context: Context) {

    private var sharedPrefManager: SharedPrefManager? = null
    private var transactionManager: TransactionManager? = null
    fun getSharedPrefManager(): SharedPrefManager {
        if(sharedPrefManager == null) {
            sharedPrefManager = SharedPrefManager.initSharedPrefManager(context)
        }
        return sharedPrefManager as SharedPrefManager
    }

    fun getTransactionManager(): TransactionManager {
        if(transactionManager == null) {
            transactionManager = TransactionManagerImpl()
        }
        return transactionManager as TransactionManager
    }
}