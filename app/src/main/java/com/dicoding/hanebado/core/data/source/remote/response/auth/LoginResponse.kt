package com.dicoding.hanebado.core.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

//	@field:SerializedName("message")
//	val message: String? = null
)
