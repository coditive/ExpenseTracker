package com.syrous.expensetracker.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.LayoutSplashScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {

    private lateinit var binding: LayoutSplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.Default) {
            delay(1000)
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            finish()
        }

    }
}