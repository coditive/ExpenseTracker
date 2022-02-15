package com.syrous.expensetracker.addusertransaction

import android.content.Context
import android.text.Selection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.syrous.expensetracker.R
import com.syrous.expensetracker.addusertransaction.UserTransactionVH.*
import com.syrous.expensetracker.databinding.*
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.utils.Constants

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
                AddDescription(binding, parent.context)
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
                binding.amountEt.clearFocus()
                val amountString = binding.amountEt.text.toString().split(Constants.rupeeSign)[1]
                val amountValue = if(amountString.startsWith(" ")) {
                    amountString.trimStart().toInt()
                } else amountString.toInt()
                clickCallback.amountNextButtonClicked(amountValue)
            }

            binding.apply {
                amountEt.requestFocus()
                amountEt.setText(Constants.rupeeSign + " ")
                Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                amountEt.addTextChangedListener { text ->
                    if (!text.toString().startsWith(Constants.rupeeSign)) {
                        amountEt.setText(Constants.rupeeSign + " ")
                        Selection.setSelection(amountEt.text, amountEt.text?.length!!)
                    }
                }

                amountEt.setOnEditorActionListener { _, actionId, _ ->
                    if(actionId == EditorInfo.IME_ACTION_DONE) {
                        binding.amountEt.clearFocus()
                        val amountString = binding.amountEt.text.toString().split(Constants.rupeeSign)[1]
                        val amountValue = if(amountString.startsWith(" ")) {
                            amountString.trimStart().toInt()
                        } else amountString.toInt()
                        clickCallback.amountNextButtonClicked(amountValue)
                        true
                    } else false
                }

                if (transactionCategory == Category.INCOME)
                    binding.spentHeader.text = "How much did you \nearned?"
            }
        }
    }

    class AddDescription(private val binding: LayoutAddDescriptionBinding, private val context: Context) :
        UserTransactionVH(binding.root) {

        fun bind(clickCallback: AddTransactionBottomSheetCallback) {
            binding.apply {
                
                calendarIconButton.setOnClickListener {
                    descriptionEt.clearFocus()
                    clickCallback.calendarIconClicked(object: CalendarDismissCallback {
                        override fun dialogDismissed() {
                            calendarIconButton.playAnimation()
                        }

                        override fun dateSelected() {
                            calendarIconButton.setAnimation(R.raw.calendar_blue)
                        }

                    })
                }

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


