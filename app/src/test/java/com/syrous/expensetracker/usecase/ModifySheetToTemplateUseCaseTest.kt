package com.syrous.expensetracker.usecase

import com.syrous.expensetracker.data.remote.SheetApiRequest
import com.syrous.expensetracker.utils.SharedPrefManager
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import javax.inject.Named

class ModifySheetToTemplateUseCaseTest {

    private lateinit var useCase: ModifySheetToTemplateUseCase
    private val sharedPrefManager: SharedPrefManager = mockk()
    private val sheetApiRequest: SheetApiRequest = mockk()
    private val apiKey = ""

    @Before
    fun setUp() {
        useCase = ModifySheetToTemplateUseCase(sharedPrefManager, sheetApiRequest, apiKey)
    }

}