package com.syrous.expensetracker

import android.app.Application

class ExpenseTrackerApplication: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent(this)
    }
}