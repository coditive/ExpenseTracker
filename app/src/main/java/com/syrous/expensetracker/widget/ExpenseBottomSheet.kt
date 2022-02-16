package com.syrous.expensetracker.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutExpenseWidgetInputBinding
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ExpenseBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutExpenseWidgetInputBinding

    private val viewModel: ExpenseTrackerWidgetVMImpl by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutExpenseWidgetInputBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTagList().asLiveData().observe(viewLifecycleOwner) { subCategoryList ->
            if (subCategoryList.isNotEmpty())
                setUpChips(subCategoryList)
        }

        binding.apply {
            categorySwitcher.setOnCheckedChangeListener { isChecked ->
                if (isChecked) viewModel.setTransactionCategory(Category.INCOME)
                else viewModel.setTransactionCategory(Category.EXPENSE)
            }

            amountEt.requestFocus()
            amountEt.addTextChangedListener { text ->
                if (!text.toString().startsWith(Constants.rupeeSign)) {
                    amountEt.setText(Constants.rupeeSign + Constants.blankSpace + text.toString())
                    Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                }
            }

            amountEt.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.descriptionEt.requestFocus()
                    val amountString =
                        binding.amountEt.text.toString().split(Constants.rupeeSign)[1]
                    val amountValue = if (amountString.startsWith(" ")) {
                        amountString.trimStart().toInt()
                    } else amountString.toInt()
                    viewModel.setAmount(amountValue)
                    true
                } else false
            }

            descriptionEt.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.descriptionEt.clearFocus()
                    viewModel.setDescription(binding.descriptionEt.text.toString())
                    true
                } else false
            }

            saveButton.setOnClickListener {
                viewModel.addUserTransaction()
                Toast.makeText(requireContext(), "Your Transaction is stored", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
                activity?.finish()
            }

            calendarIconButton.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                picker.show(childFragmentManager, "date-picker")
                picker.addOnPositiveButtonClickListener {
                    viewModel.setDate(Date(it))
                    calendarIconButton.setAnimation(R.raw.calendar_blue)
                }
                picker.addOnDismissListener {
                    calendarIconButton.playAnimation()
                }
            }

        }
    }

    private fun setUpChips(categoryTagsList: List<SubCategoryItem>) {
        val chipList = mutableListOf<Chip>()
        binding.categoryChipLayout.removeAllViews()
        categoryTagsList.forEachIndexed { _, s ->
            val chip =
                layoutInflater.inflate(R.layout.category_type_item_chip, null, false) as Chip
            chip.text = s.itemName
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
