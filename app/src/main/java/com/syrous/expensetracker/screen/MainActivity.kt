package com.syrous.expensetracker.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.syrous.expensetracker.ExpenseTrackerApplication
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.data.local.model.Transaction
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "Main_Activity"
    private val amountNullError = "Entered amount is Zero or Null"
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var transactionManager: TransactionManager
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefManager = (this.applicationContext as ExpenseTrackerApplication).appComponent.getSharedPrefManager()
        transactionManager = (this.applicationContext as ExpenseTrackerApplication).appComponent.getTransactionManager()
        transactionAdapter = TransactionAdapter()
        binding.transactionRecyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }


    override fun onResume() {
        super.onResume()
        if(sharedPrefManager.getMonthStartBalance() != 0) {
            binding.submitMonthStartAmountButton.visibility = View.GONE
            binding.monthStartAmountTv.setText(sharedPrefManager.getMonthStartBalance().toString())
        }
        binding.submitMonthStartAmountButton.setOnClickListener {
            val amount = binding.monthStartAmountTv.text.toString().toInt()
            if (amount == 0) {
                Toast.makeText(this, amountNullError, Toast.LENGTH_SHORT).show()
            } else {
                sharedPrefManager.addMonthStartBalance(amount)
            }
            binding.submitMonthStartAmountButton.visibility = View.GONE
        }

        val transactionList = transactionManager.getAllTransactionsFromStorage()
        transactionAdapter.submitList(transactionList)
        binding.totalExpensesTv.text = abs(transactionManager.getTotalExpenses()).toString()
        binding.totalExpensesTv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        val balance = transactionManager.getTotalIncome() + transactionManager.getTotalExpenses() + sharedPrefManager.getMonthStartBalance()
        if(balance < 0){
            binding.balanceTv.text = balance.toString()
            binding.balanceTv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }else {
            binding.balanceTv.text = balance.toString()
            binding.balanceTv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }

        binding.addTransactionButton.setOnClickListener {
            val intent = Intent(this, UserTransactionActivity::class.java)
            startActivity(intent)
        }
    }
}
class TransactionManagerImpl : TransactionManager {
    private val listOfTransactions = mutableListOf<Transaction>()
    override fun addTransactionToStorage(transaction: Transaction) {
        listOfTransactions.add(transaction)
    }

    override fun getAllTransactionsFromStorage(): List<Transaction> {
        return listOfTransactions.toList()
    }

    override fun getCategorisedTransactionsFromStorage(transactionCategory: Int): List<Transaction> {
        val listOfCategorisedTransaction = mutableListOf<Transaction>()
        if(transactionCategory == 0){
            for (transaction in listOfTransactions) {
                if(transaction.amount < 0) {
                    listOfCategorisedTransaction.add(transaction)
                }
            }
        } else {
            for (transaction in listOfTransactions) {
                if(transaction.amount > 0) {
                    listOfCategorisedTransaction.add(transaction)
                }
            }
        }
        return listOfCategorisedTransaction.toList()
    }

    override fun getTotalExpenses(): Int {
        var totalExpense = 0
        for(transaction in listOfTransactions) {
            if(transaction.amount < 0) {
                totalExpense += abs(transaction.amount)
            }
        }
        return totalExpense * -1
    }

    override fun getTotalIncome(): Int {
        var totalIncome = 0
        for(transaction in listOfTransactions) {
            if(transaction.amount > 0) {
                totalIncome += abs(transaction.amount)
            }
        }
        return totalIncome
    }
}