package com.syrous.expensetracker.widget

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.syrous.expensetracker.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseTrackerWidgetAddActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val inputDialog = ExpenseBottomSheet()
        Log.d("ExpenseTrackerWidget", "Activity was launched!!!")
        inputDialog.show(supportFragmentManager, "ExpenseBottomSheetInput")
    }

}