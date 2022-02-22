package com.syrous.expensetracker.utils

import com.syrous.expensetracker.data.remote.AuthTokenApi
import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.data.remote.model.AuthToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleApisClientProviderImpl @Inject constructor(
    private val tokenInterceptor: TokenInterceptor,
    private val driveApi: DriveApi,
    private val sheetApi: SheetApi,
    private val authTokenApi: AuthTokenApi,
    private val sharedPrefManager: SharedPrefManager
) : GoogleApisClientProvider {

    override var authToken: AuthToken? = null

    override suspend fun driveApiClient(): DriveApi {
        if (authToken.isExpiredOrInvalid()) {
            authToken = getRefreshAuthToken()
            tokenInterceptor.token = authToken?.accessToken
        }
        return driveApi
    }

    override suspend fun sheetApiClient(): SheetApi {
        if (authToken.isExpiredOrInvalid()) {
            authToken = getRefreshAuthToken()
            tokenInterceptor.token = authToken?.accessToken
        }
        return sheetApi
    }

    private fun invalidateAuthToken() {
        authToken = null
    }

    private suspend fun getRefreshAuthToken(): AuthToken = withContext(Dispatchers.IO) {
        authTokenApi.getToken(
            sharedPrefManager.getServerAuthToken(),
            Constants.webClientId,
            Constants.androidClientSecret,
            Constants.refreshToken
        )
    }

    companion object {
        private fun AuthToken?.isExpiredOrInvalid() =
            this == null || this.expiresIn < System.currentTimeMillis()
    }
}