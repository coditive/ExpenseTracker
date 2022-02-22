package com.syrous.expensetracker.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.AuthTokenApi
import com.syrous.expensetracker.databinding.ActivitySignInBinding
import com.syrous.expensetracker.home.HomeActivity
import com.syrous.expensetracker.service.enqueueSpreadSheetSyncWork
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import com.syrous.expensetracker.utils.TokenInterceptor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private val REQ_ONE_TAP = 2
    private val TAG = "SIGN IN ACTIVITY"
    private lateinit var binding: ActivitySignInBinding

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @Inject
    lateinit var tokenInterceptor: TokenInterceptor

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var authApi: AuthTokenApi

    private val userLoginResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(Constants.webClientId)
            .requestScopes(
                Scope(resources.getString(R.string.appdata_scope)),
                Scope(resources.getString(R.string.file_scope)),
                Scope(resources.getString(R.string.install_scope)),
                Scope(resources.getString(R.string.sheet_scope))
            )
            .build()

        signInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)


        binding.googleSignButton.setOnClickListener {
            beginSignIn()
        }

    }

    private fun beginSignIn() {
        userLoginResultContract.launch(signInClient.signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            lifecycleScope.launchWhenCreated {
                account.serverAuthCode?.let {
                   val authCred = authApi.getToken(
                        it,
                        Constants.webClientId,
                        Constants.androidClientSecret,
                        Constants.authorizationCode
                    )
                    sharedPrefManager.storeRefreshToken(authCred.refreshToken!!)
                    tokenInterceptor.token = authCred.accessToken
                    startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                }
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code= ${e.status}" + e.printStackTrace())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workManager.enqueueSpreadSheetSyncWork()
    }
}