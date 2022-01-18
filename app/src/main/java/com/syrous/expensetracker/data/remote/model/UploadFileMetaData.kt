package com.syrous.expensetracker.data.remote.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

data class UploadFileMetaData (
    val name: String,
    val mimeType: String,
    val description: String
    ): RequestBody() {
    override fun contentType(): MediaType = "application/json".toMediaType()

    private val metaData = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build().adapter(UploadFileMetaData::class.java).toJson(this)

    override fun writeTo(sink: BufferedSink) {
        sink.write(metaData.toByteArray())
    }

}