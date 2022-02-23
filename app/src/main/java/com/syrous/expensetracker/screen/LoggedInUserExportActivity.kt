package com.syrous.expensetracker.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import com.syrous.expensetracker.databinding.ActivityLoggedInUserExportBinding
import com.syrous.expensetracker.service.enqueueSpreadSheetSyncWork
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoggedInUserExportActivity: AppCompatActivity() {


    private lateinit var binding: ActivityLoggedInUserExportBinding

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInUserExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    override fun onResume() {
        super.onResume()

        binding.syncNowButton.setOnClickListener {
            workManager.enqueueSpreadSheetSyncWork()
            finish()
        }
    }
}