package com.syrous.expensetracker.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syrous.expensetracker.ExpenseTrackerApplication
import com.syrous.expensetracker.databinding.LayoutAddUserTransactionBinding
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Transaction
import java.util.*
import kotlin.random.Random

class UserTransactionActivity: AppCompatActivity() {

    private lateinit var binding: LayoutAddUserTransactionBinding
    private var transactionCategory = 0
    private lateinit var transactionManager: TransactionManager
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
            addUserTransaction()
            finish()
        }

        binding.expenseCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                transactionCategory = 0
                binding.incomeCheckBox.isChecked = false
            }
        }

        binding.incomeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                transactionCategory = 1
                binding.expenseCheckBox.isChecked = false
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

    private fun addUserTransaction() {
        val amount = binding.amountEt.text.toString()
        val category = "Personal"
        val description = binding.descriptionEt.text.toString()
        val date = binding.transactionDatePicker.dayOfMonth.toString() + "/" +
                binding.transactionDatePicker.month.toString() + "/" +
                binding.transactionDatePicker.year.toString()

        val transaction = transactionValuesValidator(amount, category, description, date)
        if (transaction != null)
            transactionManager.addTransactionToStorage(transaction)
    }

    private fun transactionValuesValidator(
        amount: String, category: String,
        description: String, date: String
    ): Transaction? {
        if (amount.isEmpty()) return null

        if (category.isEmpty()) return null

        if (description.isEmpty()) return null

        if (date.isEmpty()) return null

        return createTransaction(amount, category, description, date)
    }

    private fun createTransaction(
        amount: String,
        category: String,
        description: String,
        date: String
    ): Transaction {
        val amountInt = if(transactionCategory == 0) amount.toInt() * -1 else amount.toInt()
        return Transaction(
            Random.nextInt(),
            amountInt,
            date,
            category,
            description,
            System.currentTimeMillis()
        )
    }
}