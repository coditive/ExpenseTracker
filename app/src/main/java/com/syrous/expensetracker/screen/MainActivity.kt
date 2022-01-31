package com.syrous.expensetracker.screen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.work.WorkManager
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.ActivityMainBinding
import com.syrous.expensetracker.databinding.LayoutDashboardScreenBinding
import com.syrous.expensetracker.model.DashboardCategoryItem
import com.syrous.expensetracker.service.enqueueSpreadSheetSyncWork
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ActivityMainVMImpl by viewModels()

    private val STORAGE_REQ_CODE = 1121

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        val navController = Navigation.findNavController(this, R.id.nav_fragment)
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.title) {
                "Overview" -> navController.navigate(R.id.fragmentDashboard)
                "Add" -> navController.navigate(R.id.userTransactionFragment)
                else -> {

                }
            }
            true
        }

//        binding.apply {
//            addTransactionButton.setOnClickListener {
//                val intent = Intent(this@MainActivity, UserTransactionActivity::class.java)
//                startActivity(intent)
//            }

//            exportTransactionButton.setOnClickListener {
//                if (ContextCompat.checkSelfPermission(
//                        this@MainActivity,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    )
//                    != PackageManager.PERMISSION_GRANTED
//                ) {
//
//                    ActivityCompat.requestPermissions(
//                        this@MainActivity,
//                        arrayOf(
//                            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                        ),
//                        STORAGE_REQ_CODE
//                    )
//                } else {
//                    viewModel.searchFolderOrCreate(this@MainActivity)
//                }
//            }
//
//            transactionRecyclerView.apply {
//                adapter = transactionAdapter
//                layoutManager = LinearLayoutManager(this@MainActivity)
//            }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.enqueueSpreadSheetSyncWork()
    }
}
