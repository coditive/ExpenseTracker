package com.syrous.expensetracker.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.screen.usertransaction.UserTransactionActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ActivityMainVM by viewModels()
    private val transactionAdapter = TransactionAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onResume() {
        super.onResume()

        binding.apply {
            addTransactionButton.setOnClickListener {
                val intent = Intent(this@MainActivity, UserTransactionActivity::class.java)
                startActivity(intent)
            }

            transactionRecyclerView.apply {
                adapter = transactionAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }

        viewModel.transactionsList.asLiveData().observe(this) {
            transactionList -> transactionAdapter.submitList(transactionList)
        }
    }
}
