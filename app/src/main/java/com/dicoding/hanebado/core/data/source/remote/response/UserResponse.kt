package com.dicoding.hanebado.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("isActivated")
	val isActivated: Boolean? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
