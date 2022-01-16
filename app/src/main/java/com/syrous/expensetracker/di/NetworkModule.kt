package com.syrous.expensetracker.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.syrous.expensetracker.BuildConfig
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.ApiRequest
import com.syrous.expensetracker.data.remote.AuthTokenRequest
import com.syrous.expensetracker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(httpLoggingInterceptor)
        }
        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi
        .Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideApiRequest(
        @ApplicationContext context: Context, moshi: Moshi,
        okHttpClient: OkHttpClient
    ): ApiRequest = Retrofit.Builder()
        .baseUrl(context.resources.getString(R.string.upload_uri))
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(ApiRequest::class.java)

    @Singleton
    @Provides
    fun provideAuthTokenRequest(
        @ApplicationContext context: Context, moshi: Moshi,
        okHttpClient: OkHttpClient
    ): AuthTokenRequest = Retrofit.Builder()
        .baseUrl(context.resources.getString(R.string.token_endpoint))
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(AuthTokenRequest::class.java)

}