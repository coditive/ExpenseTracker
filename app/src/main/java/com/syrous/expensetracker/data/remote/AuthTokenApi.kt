package com.syrous.expensetracker.data.remote

import com.syrous.expensetracker.data.remote.model.AuthToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthTokenApi {

    @POST("token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("code") serverAuthCode: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String
    ): AuthToken

    @POST("token")
    @FormUrlEncoded
    suspend fun getRefreshedToken(
        @Field("refresh_token") serverAuthCode: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String
    ): AuthToken



}