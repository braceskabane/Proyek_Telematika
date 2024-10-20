package com.dicoding.hanebado.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class OtpResponse(

	@field:SerializedName("message")
	val message: String? = null
)
