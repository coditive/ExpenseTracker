package com.syrous.expensetracker.screen.addusertransaction

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.syrous.expensetracker.databinding.LayoutAddUserTransactionBinding
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.screen.addusertransaction.ViewState.Error
import com.syrous.expensetracker.screen.addusertransaction.ViewState.Success
import com.syrous.expensetracker.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class UserTransactionFragment : Fragment() {

    private lateinit var binding: LayoutAddUserTransactionBinding

    private var categoryTagList = mutableListOf<String>()

    private val viewModel: UserTransactionVMImpl by viewModels()

    private val dateFormatter = SimpleDateFormat(Constants.datePattern, Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAddUserTransactionBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.asLiveData().observe(viewLifecycleOwner) { viewState ->
            if (viewState is Success) {
                binding.apply {

                    val cal = Calendar.getInstance()
                    cal.time = viewState.date

                    transactionDatePicker.init(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH),
                        null
                    )

                    if (viewState.category == Category.EXPENSE)
                        expenseCheckBox.isChecked = true
                    else
                        incomeCheckBox.isChecked = true

                }


            } else if (viewState is Error) {
                when (viewState.element) {
                    //TODO(Alert dialog)
                }
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryTagList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        viewModel.getTagList().asLiveData().observe(viewLifecycleOwner) { subCategoryList ->
            categoryTagList = subCategoryList.toMutableList()
            adapter.clear()
            adapter.addAll(categoryTagList)
            adapter.notifyDataSetChanged()
        }


        binding.apply {

            addUserTransactionButton.setOnClickListener {
                viewModel.addUserTransaction()
            }

            amountEt.addTextChangedListener { text: Editable? ->
                viewModel.setAmount(text.toString().toInt())
            }

            descriptionEt.addTextChangedListener { text: Editable? ->
                viewModel.setDescription(text.toString())
            }

            transactionDatePicker.setOnDateChangedListener { _, year, month, day ->
                val dateString = "$day/$month/$year"
                viewModel.setDate(dateFormatter.parse(dateString)!!)
            }


            categoryTagSpinner.adapter = adapter
            categoryTagSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        viewModel.setCategoryTag(categoryTagList[position])
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }


            expenseCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setTransactionCategory(Category.EXPENSE)
                    incomeCheckBox.isChecked = false
                } else {
                    incomeCheckBox.isChecked = true
                    expenseCheckBox.isChecked = false
                    viewModel.setTransactionCategory(Category.INCOME)
                }
            }

            incomeCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setTransactionCategory(Category.INCOME)
                    expenseCheckBox.isChecked = false
                } else {
                    expenseCheckBox.isChecked = true
                    incomeCheckBox.isChecked = false
                    viewModel.setTransactionCategory(Category.EXPENSE)
                }
            }
        }
    }
}
