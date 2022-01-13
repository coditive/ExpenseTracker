package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.model.RemoteTransaction
import com.syrous.expensetracker.data.remote.model.RemoteUserTransaction
import com.syrous.expensetracker.utils.Constants
import retrofit2.http.*
import java.util.*

interface ApiRequest {

    @GET("sheets/{sheet_name}")
    suspend fun getDataFromSheet(
        @Path("sheet_name") sheetName: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<RemoteUserTransaction>

    @GET("search")
    suspend fun searchDataInSheet(
        @Query("id") id: Long? = null,
        @Query("description") description: String? = null,
        @Query("categoryTag") categoryTag: String? = null,
        @Query("transactionCategory") transactionCategory: TransactionCategory? = null,
        @Query("date") date: Date
    ): List<RemoteUserTransaction>

    @POST
    suspend fun addRowInSheet(remoteTransaction: RemoteTransaction)

    @PUT("{column_name}/{value}")
    suspend fun updateSheetValue(
        @Path("column_name") propertyName: String,
        @Path("value") value: String
    )

    @DELETE("{column_name}/{value}")
    suspend fun deleteRecord(
        @Path("column_name") propertyName: String,
        @Path("value") value: String
    )

}