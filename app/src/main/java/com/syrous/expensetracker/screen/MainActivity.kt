package com.syrous.expensetracker.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.screen.usertransaction.UserTransactionActivity
import com.syrous.expensetracker.upload.UploadUserTransactionUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ActivityMainVM by viewModels()

    private val STORAGE_REQ_CODE = 1121

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

            exportTransactionButton.setOnClickListener {
                 if(ContextCompat.checkSelfPermission(this@MainActivity,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE)
                     != PackageManager.PERMISSION_GRANTED){

                     ActivityCompat.requestPermissions(this@MainActivity,
                         arrayOf(
                             Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                             Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE
                         ),
                         STORAGE_REQ_CODE
                     )
                 } else {
                     viewModel.exportDataToDrive(this@MainActivity)
                 }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
