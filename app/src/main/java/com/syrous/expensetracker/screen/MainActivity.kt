package com.syrous.expensetracker.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.syrous.expensetracker.ExpenseTrackerApplication
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Transaction
import com.syrous.expensetracker.utils.SharedPrefManager

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
        binding.submitMonthStartAmountButton.setOnClickListener {
            val amount = binding.monthStartAmountTv.text.toString().toInt()
            if (amount == 0) {
                Toast.makeText(this, amountNullError, Toast.LENGTH_SHORT).show()
            } else {
                sharedPrefManager.addMonthStartBalance(amount)
            }
        }

        val transactionList = transactionManager.getTransactionsFromStorage(0)
        transactionAdapter.submitList(transactionList)
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

    override fun getTransactionsFromStorage(transactionCategory: Int): List<Transaction> {
        return listOfTransactions.toList()
    }
}