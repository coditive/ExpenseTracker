package com.syrous.expensetracker.widget

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseTrackerWidgetAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        hideSystemUI()
        val inputDialog = ExpenseBottomSheet()
        inputDialog.show(supportFragmentManager, "ExpenseBottomSheetInput")
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

}