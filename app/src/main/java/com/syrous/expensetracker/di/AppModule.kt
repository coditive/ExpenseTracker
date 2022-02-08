package com.syrous.expensetracker.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.syrous.expensetracker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    companion object {
        @Provides
        @Singleton
        fun provideWorkConfiguration(hiltWorkerFactory: HiltWorkerFactory): Configuration =
            Configuration
                .Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setWorkerFactory(hiltWorkerFactory)
                .build()

        @Provides
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @Named("apiKey")
        @Provides
        fun provideApiKey(): String = Constants.apiKey
    }
}