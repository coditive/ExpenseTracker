package com.syrous.expensetracker.addusertransaction

import android.animation.Animator
import android.content.Context
import android.os.Handler
import android.text.Selection
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.DateSelector
import com.google.android.material.datepicker.MaterialCalendar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.syrous.expensetracker.R
import com.syrous.expensetracker.addusertransaction.UserTransactionVH.*
import com.syrous.expensetracker.databinding.*
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.SubCategoryItem
import com.syrous.expensetracker.utils.Constants
import kotlinx.coroutines.delay

class UserTransactionAdapter(
    private val clickCallback: AddTransactionBottomSheetCallback,
    private val subCategoryListProvider: SubCategoryListProvider,
    private val transactionCategory: Category
) : ListAdapter<AddTransactionState, UserTransactionVH>(CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTransactionVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val binding = LayoutAmountSpentEntryBinding.inflate(layoutInflater, parent, false)
                AddAmountVH(binding)
            }
            1 -> {
                val binding = LayoutAddCategoriesBinding.inflate(layoutInflater, parent, false)
                AddCategories(binding, layoutInflater, parent.context)
            }
            2 -> {
                val binding = LayoutAddDescriptionBinding.inflate(layoutInflater, parent, false)
                AddDescription(binding)
            }
            else -> throw RuntimeException()
        }
    }

    override fun onBindViewHolder(holder: UserTransactionVH, position: Int) {
        when (holder) {
            is AddAmountVH -> holder.bind(clickCallback, transactionCategory)
            is AddCategories -> holder.bind(clickCallback, subCategoryListProvider, transactionCategory)
            is AddDescription -> holder.bind(clickCallback)
        }
    }

    override fun getItemViewType(position: Int): Int = position

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<AddTransactionState>() {
            override fun areItemsTheSame(
                oldItem: AddTransactionState,
                newItem: AddTransactionState
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: AddTransactionState,
                newItem: AddTransactionState
            ): Boolean = true
        }
    }
}

sealed class UserTransactionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class AddAmountVH(private val binding: LayoutAmountSpentEntryBinding) :
        UserTransactionVH(binding.root) {

        fun bind(clickCallback: AddTransactionBottomSheetCallback, transactionCategory: Category) {
            binding.nextButton.setOnClickListener {
                val amount = binding.amountEt.text.toString().split(" ")
                if (amount.size >= 2 && amount[1].isNotEmpty() && amount[1].isDigitsOnly())
                    clickCallback.amountNextButtonClicked(amount = amount[1].toInt())
                else {
                    binding.apply {
                        amountEt.error = "Please Enter Numeric Value"
                        amountEt.setText(Constants.rupeeSign + " ")
                        Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                    }
                }
                binding.amountEt.clearFocus()
            }

            binding.apply {
                amountEt.setText(Constants.rupeeSign + " ")
                Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                amountEt.addTextChangedListener { text ->
                    if (!text.toString().startsWith(Constants.rupeeSign)) {
                        amountEt.setText(Constants.rupeeSign + " ")
                        Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                    }
                }

                if (transactionCategory == Category.INCOME)
                    binding.spentHeader.text = "How much did you \nearned?"
            }
        }
    }

    class AddDescription(private val binding: LayoutAddDescriptionBinding) :
        UserTransactionVH(binding.root) {

        fun bind(clickCallback: AddTransactionBottomSheetCallback) {
            binding.apply {
                calendarIconButton.setOnClickListener {
                    calendarIconButton.playAnimation()
                }

                calendarIconButton.addAnimatorListener(object: Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        clickCallback.calendarIconClicked()
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                })

                saveButton.setOnClickListener {
                    if(binding.descriptionEt.text.toString().isEmpty())
                        binding.descriptionEt.error = "Description cannot be blank"
                    else clickCallback.descriptionSaveButtonClicked(binding.descriptionEt.text.toString())
                }
            }
        }
    }

    class AddCategories(
        private val binding: LayoutAddCategoriesBinding,
        private val layoutInflater: LayoutInflater,
        private val context: Context
    ) :
        UserTransactionVH(binding.root) {

        fun bind(
            clickCallback: AddTransactionBottomSheetCallback,
            subCategoryListProvider: SubCategoryListProvider,
            transactionCategory: Category
        ) {
            if (transactionCategory == Category.INCOME)
                binding.categoriesHeader.text = "Where did you \nearned from?"
            setUpChips(clickCallback, subCategoryListProvider, context)
        }

        private fun setUpChips(
            clickCallback: AddTransactionBottomSheetCallback,
            subCategoryListProvider: SubCategoryListProvider,
            context: Context
        ) {
            val chipList = mutableListOf<Chip>()
            binding.subCategoryChipGroup.removeAllViews()
            val subCategoryItemList = subCategoryListProvider.getSubCategoryList()
            subCategoryItemList.forEachIndexed { _, s ->
                val chip = layoutInflater.inflate(
                    R.layout.category_type_item_chip,
                    null,
                    false
                ) as Chip

                chip.text = s.itemName
                chip.setOnCheckedChangeListener { _, b ->
                    if (b) {
                        clickCallback.subCategoryClicked(s)
                        chip.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_blue))
                    }
                }
                binding.subCategoryChipGroup.addView(chip)
                chipList.add(chip)
            }
        }
    }

}


