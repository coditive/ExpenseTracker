package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.remote.model.SpreadSheetBatchUpdateRequest
import com.syrous.expensetracker.data.remote.model.SpreadSheetResponse
import retrofit2.http.*

interface SheetApiRequest {

    @GET("{spreadSheetId}")
    suspend fun getSpreadSheetData(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String
    ): SpreadSheetResponse

    @POST("{spreadSheetId}:batchUpdate")
    suspend fun updateSpreadSheetToFormat(
        @Header("Authorization") authToken: String,
        @Path("spreadSheetId") spreadSheetId: String,
        @Query("key") apiKey: String,
        @Body spreadSheetBatchUpdateRequest: SpreadSheetBatchUpdateRequest
    )

}