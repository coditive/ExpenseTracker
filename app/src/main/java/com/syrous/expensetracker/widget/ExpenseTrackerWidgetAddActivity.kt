package com.syrous.expensetracker.widget

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.syrous.expensetracker.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseTrackerWidgetAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        hideSystemUI()
        val inputDialog = ExpenseBottomSheet()
        Log.d("ExpenseTrackerWidget", "Activity was launched!!!")
        inputDialog.show(supportFragmentManager, "ExpenseBottomSheetInput")

    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}