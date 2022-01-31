package com.syrous.expensetracker.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutSplashScreenBinding
import com.syrous.expensetracker.service.enqueueDashboardItemSortWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {

    private lateinit var binding: LayoutSplashScreenBinding

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.Default) {
            delay(5000)
            workManager.enqueueDashboardItemSortWorker()
            delay(1000)
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            finish()
        }

    }
}