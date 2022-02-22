package com.syrous.expensetracker.usecase

import android.content.Context
import com.syrous.expensetracker.data.remote.DriveApi
import com.syrous.expensetracker.datainterface.TransactionManager
import com.syrous.expensetracker.model.Category
import com.syrous.expensetracker.model.UserTransaction
import com.syrous.expensetracker.usecase.UseCaseResult.Failure
import com.syrous.expensetracker.usecase.UseCaseResult.Success
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class UploadUserTransactionUseCaseTest {

    private lateinit var useCase: UploadUserTransactionUseCase
    private val transactionManager: TransactionManager = mockk()
    private val api: DriveApi = mockk()
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val apiKey = ""
    private val context: Context = mockk()
    private val fileName = ""
    private val description = ""

    @Before
    fun setUp() {
        useCase = UploadUserTransactionUseCase(transactionManager, api, sharedPrefManager, apiKey)
    }

    @Test
    fun `when transactionList is empty`() = runBlocking {
        coEvery { transactionManager.getAllTransactionListFromStorage() } returns emptyList()

        val result = useCase.uploadUserTransactionToDrive(context, fileName, description)
        print(result)
        assert(result is Success)
    }

    @Test
    fun `when transactionList is not Empty but file creation is failed`() = runBlocking {
        coEvery { transactionManager.getAllTransactionListFromStorage() } returns buildUserTransaction(10)
        every { context.applicationContext.getFileStreamPath(fileName) } throws IOException("File creation failed!!!")

        val result = useCase.uploadUserTransactionToDrive(context, fileName, description)
        print(result)
        assert(result is Failure)
    }

    @Test
    fun `when transactionList is not Empty but context returned empty path`() = runBlocking {
        coEvery { transactionManager.getAllTransactionListFromStorage() } returns buildUserTransaction(10)
        every { context.applicationContext.getFileStreamPath(fileName) } returns File("")

        val result = useCase.uploadUserTransactionToDrive(context, fileName, description)
        print(result)
        assert(result is Failure)
    }

    private fun buildUserTransaction(count: Int) = mutableListOf<UserTransaction>().apply {
        repeat(count) {
            add(
                UserTransaction(
                    id = abs(Random.nextLong()),
                    amount = abs(Random.nextInt()),
                    description = "",
                    date = Date(0L),
                    category = Category.EXPENSE,
                    categoryTag = ""
                )
            )
        }
    }

}