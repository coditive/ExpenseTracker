package com.syrous.expensetracker.screen

import android.R.attr.data
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.syrous.expensetracker.R
import com.syrous.expensetracker.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private val REQ_ONE_TAP = 2
    private val TAG = "SIGN IN ACTIVITY"
    private lateinit var binding: ActivitySignInBinding

    private val userLoginResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestEmail()
            .build()

        signInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        binding.googleSignButton.setSize(SignInButton.SIZE_STANDARD)
        binding.googleSignButton.setOnClickListener{
            beginSignIn()
        }
    }

    private fun beginSignIn() {
        userLoginResultContract.launch(signInClient.signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "account - ${account.idToken}")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.printStackTrace())

        }
    }


}