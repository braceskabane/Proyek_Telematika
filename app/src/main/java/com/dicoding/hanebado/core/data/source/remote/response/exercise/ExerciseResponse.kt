package com.dicoding.hanebado.core.data.source.remote.response.exercise

import com.google.gson.annotations.SerializedName

data class ExerciseResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("difficultyXP")
	val difficultyXP: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
