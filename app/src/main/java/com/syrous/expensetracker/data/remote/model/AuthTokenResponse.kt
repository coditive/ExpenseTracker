package com.syrous.expensetracker.data.remote.model

import com.squareup.moshi.Json

data class AuthTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    val scope: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "expires_in") val expiresIn: Int,
    @Json(name = "refresh_token") val refreshToken: String?
)