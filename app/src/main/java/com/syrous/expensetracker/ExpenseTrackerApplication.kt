package com.syrous.expensetracker

import android.app.Application
import android.content.ComponentCallbacks
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class ExpenseTrackerApplication: Application(), Configuration.Provider {

    private val TAG = ExpenseTrackerApplication::class.java.name

    @Inject
    lateinit var workConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application Started!!!")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application Terminated!!!")
    }

    override fun getWorkManagerConfiguration(): Configuration = workConfiguration
}