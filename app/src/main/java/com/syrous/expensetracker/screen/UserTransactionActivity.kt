package com.syrous.expensetracker.screen

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.syrous.expensetracker.ExpenseTrackerApplication
import com.syrous.expensetracker.databinding.LayoutAddUserTransactionBinding
import com.syrous.expensetracker.datainterface.CategoryManager
import com.syrous.expensetracker.datainterface.TransactionManager
import java.util.*

class UserTransactionActivity: AppCompatActivity() {

    private lateinit var binding: LayoutAddUserTransactionBinding
    private var transactionCategory = 0
    private lateinit var transactionManager: TransactionManager
    private val categoryManager = CategoryManagerImpl() as CategoryManager
    private lateinit var categoryArrayAdapter: ArrayAdapter<String>
    private var category: String = ""
    private var isExpenseChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAddUserTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionManager = (this.applicationContext as ExpenseTrackerApplication).appComponent.getTransactionManager()
    }

    override fun onResume() {
        super.onResume()
        setUI()
        binding.addUserTransactionButton.setOnClickListener {
//            addUserTransaction()
            finish()
        }

        binding.expenseCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                transactionCategory = 0
                binding.incomeCheckBox.isChecked = false
                isExpenseChecked = true
                categoryArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    categoryManager.getExpenseCategories())
                categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = categoryArrayAdapter
            }
        }

        binding.incomeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                transactionCategory = 1
                binding.expenseCheckBox.isChecked = false
                isExpenseChecked = false
                categoryArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    categoryManager.getIncomeCategories())
                categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = categoryArrayAdapter
            }
        }

        binding.categorySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(isExpenseChecked) {
                    category = categoryManager.getExpenseCategories()[p2]
                } else {
                    category = categoryManager.getIncomeCategories()[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setUI() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.transactionDatePicker.init(year, month, day, null)

    }

//    private fun addUserTransaction() {
//        val amount = binding.amountEt.text.toString()
//        val description = binding.descriptionEt.text.toString()
//        val date = binding.transactionDatePicker.dayOfMonth.toString() + "/" +
//                binding.transactionDatePicker.month.toString() + "/" +
//                binding.transactionDatePicker.year.toString()
//
//        val transaction = transactionValuesValidator(amount, category, description, date)
//        if (transaction != null)
//            transactionManager.addTransactionToStorage(transaction)
//    }

//    private fun transactionValuesValidator(
//        amount: String, category: String,
//        description: String, date: String
//    ): Transaction? {
//        if (amount.isEmpty()) return null
//
//        if (category.isEmpty()) return null
//
//        if (description.isEmpty()) return null
//
//        if (date.isEmpty()) return null
//
//        return createTransaction(amount, category, description, date)
//    }

//    private fun createTransaction(
//        amount: String,
//        category: String,
//        description: String,
//        date: String
//    ): Transaction {
//        val amountInt = if(transactionCategory == 0) amount.toInt() * -1 else amount.toInt()
//        return Transaction(
//            Random.nextInt(),
//            amountInt,
//            date,
//            //TODO : Add category id to db,
//            description,
//            Date()
//        )
//    }
}

class CategoryManagerImpl: CategoryManager {
    override fun getExpenseCategories(): Array<String> {
        val categoryList = mutableListOf<String>()
        categoryList.add("Food")
        categoryList.add("Gifts")
        categoryList.add("Medical/Health")
        categoryList.add("Home")
        categoryList.add("Personal")
        categoryList.add("MF")
        return categoryList.toTypedArray()
    }

    override fun getIncomeCategories(): Array<String>{
        val categoryList = mutableListOf<String>()
        categoryList.add("Savings")
        categoryList.add("Paycheck")
        categoryList.add("Bonus")
        categoryList.add("Interest")
        categoryList.add("Other")
        return categoryList.toTypedArray()
    }

}