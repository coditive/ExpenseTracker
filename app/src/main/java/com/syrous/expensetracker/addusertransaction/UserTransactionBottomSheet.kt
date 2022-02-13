package com.syrous.expensetracker.addusertransaction

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutAddUserTransactionBinding
import com.syrous.expensetracker.home.HomeActivity
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.*


@AndroidEntryPoint
class UserTransactionBottomSheet : BottomSheetDialogFragment(), AddTransactionBottomSheetCallback, SubCategoryListProvider {

    private lateinit var binding: LayoutAddUserTransactionBinding

    private val viewModel: UserTransactionVMImpl by viewModels()

    private lateinit var userTransactionAdapter: UserTransactionAdapter

    private lateinit var transactionCategory: Category

    private var subCategoryItemList: List<SubCategoryItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        WindowCompat.setDecorFitsSystemWindows(dialog?.window!!, false)
        binding = LayoutAddUserTransactionBinding.inflate(inflater, container, false)
        val category = arguments?.getString("Category")
        transactionCategory = if(category == "Income") {
            viewModel.setTransactionCategory(Category.INCOME)
            Category.INCOME
        } else{
            viewModel.setTransactionCategory(Category.EXPENSE)
            Category.EXPENSE
        }
        userTransactionAdapter = UserTransactionAdapter(this, this, transactionCategory)
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val behavior = BottomSheetBehavior.from(binding.standardBottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addTransactionStateList = mutableListOf<AddTransactionState>()
        addTransactionStateList.add(AddTransactionState("Amount"))
        addTransactionStateList.add(AddTransactionState("Categories"))
        addTransactionStateList.add(AddTransactionState("Description"))

        userTransactionAdapter.submitList(addTransactionStateList)

        binding.addUserTransactionRecyclerView.apply {
            adapter = userTransactionAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        viewModel.getTagList().asLiveData().observe(viewLifecycleOwner) {
            subCategoryItemList = it
        }

    }

    override fun amountNextButtonClicked(amount: Int) {
        viewModel.setAmount(amount)
        binding.addUserTransactionRecyclerView.smoothScrollToPosition(1)
    }

    override fun descriptionSaveButtonClicked(description: String) {
        viewModel.setDescription(description)
        viewModel.addUserTransaction()
        lifecycleScope.launchWhenCreated {
            delay(1000)
            dialog?.dismiss()
        }
        (requireActivity() as HomeActivity).displaySuccessSnackbar("Expense")
    }

    override fun calendarIconClicked() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.show(childFragmentManager, "date-picker")
        picker.addOnPositiveButtonClickListener {
            viewModel.setDate(Date(it))
        }
    }

    override fun subCategoryClicked(subCategory: SubCategoryItem) {
        viewModel.setSubCategoryTag(subCategory)
        lifecycleScope.launchWhenCreated {
            delay(300)
            binding.addUserTransactionRecyclerView.smoothScrollToPosition(2)
        }
    }

    override fun getSubCategoryList(): List<SubCategoryItem> = subCategoryItemList
}
