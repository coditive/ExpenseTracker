package com.syrous.expensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.utils.SharedPrefManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "Main_Activity"
    private val amountNullError = "Entered amount is Zero or Null"
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefManager = SharedPrefManager.initSharedPrefManager(this)
    }


    override fun onResume() {
        super.onResume()
        binding.monthStartAmountButton.setOnClickListener {
            val amount = binding.monthStartAmountTv.text.toString().toInt()
            if (amount == 0) {
                Toast.makeText(this, amountNullError, Toast.LENGTH_SHORT).show()
            } else {
                sharedPrefManager.addMonthStartBalance(amount)
            }
        }
    }
}