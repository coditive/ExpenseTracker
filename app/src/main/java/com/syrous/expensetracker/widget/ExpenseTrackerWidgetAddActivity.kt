package com.syrous.expensetracker.widget

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.syrous.expensetracker.R

class ExpenseTrackerWidgetAddActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputDialog = ExpenseBottomSheet()
        Log.d("ExpenseTrackerWidget", "Activity was launched!!!")
        inputDialog.dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        inputDialog.show(supportFragmentManager, "ExpenseBottomSheetInput")
    }
}