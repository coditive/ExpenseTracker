package com.syrous.expensetracker.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.syrous.expensetracker.BuildConfig
import com.syrous.expensetracker.R
import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.data.remote.AuthTokenApi
import com.syrous.expensetracker.data.remote.SheetApi
import com.syrous.expensetracker.utils.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(tokenInterceptor)
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
    ): DriveApi = Retrofit.Builder()
        .baseUrl(context.resources.getString(R.string.upload_uri))
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(DriveApi::class.java)

    @Singleton
    @Provides
    fun provideAuthTokenRequest(
        @ApplicationContext context: Context, moshi: Moshi,
        okHttpClient: OkHttpClient
    ): AuthTokenApi = Retrofit.Builder()
        .baseUrl(context.resources.getString(R.string.token_endpoint))
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(AuthTokenApi::class.java)


    @Singleton
    @Provides
    fun provideSheetApiRequest(
        @ApplicationContext context: Context, moshi: Moshi,
        okHttpClient: OkHttpClient
    ): SheetApi = Retrofit.Builder()
        .baseUrl(context.getString(R.string.sheet_api))
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(SheetApi::class.java)

}