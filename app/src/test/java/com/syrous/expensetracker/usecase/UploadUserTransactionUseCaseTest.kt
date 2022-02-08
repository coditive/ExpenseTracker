package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.DriveApiRequest
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import javax.inject.Named

class UploadUserTransactionUseCaseTest {

    private lateinit var useCase: UploadUserTransactionUseCase
    private val transactionManager: TransactionManager = mockk()
    private val apiRequest: DriveApiRequest = mockk()
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val apiKey = ""

    @Before
    fun setUp() {
        useCase = UploadUserTransactionUseCase(transactionManager, apiRequest, sharedPrefManager, apiKey)
    }

}