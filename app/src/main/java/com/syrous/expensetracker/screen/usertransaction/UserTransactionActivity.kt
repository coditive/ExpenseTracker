package com.syrous.expensetracker.screen.usertransaction

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.databinding.LayoutAddUserTransactionBinding
import com.syrous.expensetracker.screen.usertransaction.ViewState.Error
import com.syrous.expensetracker.screen.usertransaction.ViewState.Success
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class UserTransactionActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAddUserTransactionBinding

    private var categoryTagList = emptyList<String>()

    private val viewModel: UserTransactionVMImpl by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAddUserTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        viewModel.viewState.asLiveData().observe(this) { viewState ->
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

                    if (viewState.transactionCategory == TransactionCategory.EXPENSE)
                        expenseCheckBox.isChecked = true
                    else
                        incomeCheckBox.isChecked = true

                    categoryTagList = viewModel.getTagList()
                }

                if(viewState.isTransactionAdded) finish()

            } else if (viewState is Error) {
                when (viewState.element) {

                }
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryTagList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

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
                val dateFormater = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateString = "$day/$month/$year"
                viewModel.setDate(dateFormater.parse(dateString)!!)
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
                    viewModel.setTransactionCategory(TransactionCategory.EXPENSE)
                    incomeCheckBox.isChecked = false
                } else {
                    incomeCheckBox.isChecked = true
                    expenseCheckBox.isChecked = false
                    viewModel.setTransactionCategory(TransactionCategory.INCOME)
                }
                categoryTagList = viewModel.getTagList()
                adapter.clear()
                adapter.addAll(categoryTagList)
                adapter.notifyDataSetChanged()
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
                categoryTagList = viewModel.getTagList()
                adapter.clear()
                adapter.addAll(categoryTagList)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
