package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.local.model.TransactionCategory
import com.syrous.expensetracker.data.local.model.UserTransaction
import com.syrous.expensetracker.data.remote.model.*
import com.syrous.expensetracker.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap

interface DriveApiRequest {

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadFile(
        @Header("Authorization") authToken: String,
        @Query("key") apiKey: String,
        @Part(encoding = "8-bit") uploadFileMetaData: MultipartBody.Part,
        @Part(encoding = "base64") file: MultipartBody.Part
    ): BasicFileMetaData

    @POST("drive/v3/files")
    @Headers("Content-Type: application/json")
    suspend fun createFolder(
        @Header("Authorization") authToken: String,
        @Query("key") apiKey: String,
        @Body request: CreateFolderRequest
    ): BasicFileMetaData

    @GET("drive/v3/files")
    suspend fun searchFile(
        @Header("Authorization") authToken: String,
        @Query("key") apiKey: String,
        @Query("corpora") corpora: String,
        @Query("q") query: String
    ): SearchFileQueryResponse

}