package com.syrous.expensetracker.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws


@Singleton
class TokenInterceptor @Inject constructor(): Interceptor {

    var token: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        if(request.header("Authorization") == null) {
            Log.i(TAG, " intercept: no auth header present thus adding new ones")
            if(token != null) {
                Log.i(TAG, " intercept: token - $token")
                requestBuilder.addHeader("Authorization", "bearer $token")
            } else Log.e(TAG, "intercept: token is null")
        }
        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val TAG = "TokenInterceptor"
    }
}