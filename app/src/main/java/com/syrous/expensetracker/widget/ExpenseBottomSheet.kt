package com.syrous.expensetracker.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutExpenseWidgetInputBinding

class ExpenseBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: LayoutExpenseWidgetInputBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutExpenseWidgetInputBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}