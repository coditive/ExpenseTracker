package com.syrous.expensetracker

import android.app.Application
import android.content.ComponentCallbacks
import android.util.Log
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ExpenseTrackerApplication: Application() {

    private val TAG = ExpenseTrackerApplication::class.java.name

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application Started!!!")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application Terminated!!!")
    }
}