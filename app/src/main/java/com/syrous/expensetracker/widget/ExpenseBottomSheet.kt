package com.syrous.expensetracker.widget

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.databinding.LayoutExpenseWidgetInputBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutExpenseWidgetInputBinding

    val viewModel: ExpenseTrackerWidgetVMImpl by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutExpenseWidgetInputBinding.inflate(layoutInflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            viewModel.viewState.asLiveData().observe(viewLifecycleOwner) { viewState ->
                if (viewState is Success) {

                    if (viewState.transactionCategory == TransactionCategory.EXPENSE)
                        expenseCheckBox.isChecked = true
                    else
                        incomeCheckBox.isChecked = true

                    setUpChips(viewModel.getTagList())
                } else if (viewState is Error) {

                }
            }

            saveButton.setOnClickListener {
                viewModel.addUserTransaction()
                requireActivity().finish()
            }

            expenseInputTextview.addTextChangedListener { text: Editable? ->
                viewModel.setAmount(text.toString().toInt())
            }

            expenseDetailsTextview.addTextChangedListener { text: Editable? ->
                viewModel.setDescription(text.toString())
            }

            expenseCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setTransactionCategory(TransactionCategory.EXPENSE)
                    incomeCheckBox.isChecked = false
                } else {
                    incomeCheckBox.isChecked = true
                    expenseCheckBox.isChecked = false
                    viewModel.setTransactionCategory(TransactionCategory.INCOME)
                }

                setUpChips(viewModel.getTagList())
            }

            incomeCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setTransactionCategory(TransactionCategory.INCOME)
                    expenseCheckBox.isChecked = false
                } else {
                    expenseCheckBox.isChecked = true
                    incomeCheckBox.isChecked = false
                    viewModel.setTransactionCategory(TransactionCategory.EXPENSE)
                }

                setUpChips(viewModel.getTagList())
            }
        }

    }

    private fun setUpChips(categoryTagsList: List<String>) {
        val chipList = mutableListOf<Chip>()
        binding.categoryChipLayout.removeAllViews()
        categoryTagsList.forEachIndexed { index, s ->
            val chip =
                layoutInflater.inflate(R.layout.category_type_item_chip, null, false) as Chip
            chip.text = s
            val paddingInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10F,
                resources.displayMetrics
            )
            chip.setPadding(paddingInDp.toInt(), 0, paddingInDp.toInt(), 0)
            chip.setOnCheckedChangeListener { compoundButton, b ->
                if (b) viewModel.setCategoryTag(compoundButton.text.toString())
            }
            binding.categoryChipLayout.addView(chip)
            chipList.add(chip)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        activity?.finish()
    }
}
