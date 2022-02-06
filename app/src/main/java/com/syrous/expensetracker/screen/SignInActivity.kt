package com.syrous.expensetracker.screen

import android.R.attr.data
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.AuthTokenRequest
import com.syrous.expensetracker.databinding.ActivitySignInBinding
import com.syrous.expensetracker.screen.release.ReleaseMainActivity
import com.syrous.expensetracker.utils.Constants
import com.syrous.expensetracker.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.multibindings.IntKey
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
    lateinit var authRequest: AuthTokenRequest

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
                   val authCred = authRequest.getToken(
                        it,
                        Constants.webClientId,
                        Constants.androidClientSecret,
                        "authorization_code"
                    )
                    sharedPrefManager.storeUserToken(authCred.accessToken)
                    startActivity(Intent(this@SignInActivity, ReleaseMainActivity::class.java))
                }
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code= ${e.status}" + e.printStackTrace())
        }
    }


}