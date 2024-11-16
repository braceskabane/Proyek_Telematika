package com.dicoding.hanebado.core.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName

data class ResendOtpResponse(
    @field:SerializedName("message")
    val message: String
)