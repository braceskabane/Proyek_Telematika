package com.dicoding.hanebado.core.data.source.remote.response.dailyplan

import com.google.gson.annotations.SerializedName

data class AllDailyPlanResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class Meta(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("hasNextPage")
	val hasNextPage: Boolean? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("hasPreviousPage")
	val hasPreviousPage: Boolean? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ExerciseAll(

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

data class DataAll(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)

data class ExercisesItemAll(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("dailyPlanId")
	val dailyPlanId: Int? = null,

	@field:SerializedName("reps")
	val reps: Int? = null,

	@field:SerializedName("exerciseId")
	val exerciseId: Int? = null,

	@field:SerializedName("sets")
	val sets: Int? = null,

	@field:SerializedName("exercise")
	val exercise: Exercise? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order")
	val order: Int? = null,

	@field:SerializedName("isCompleted")
	val isCompleted: Boolean? = null
)

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("repeatDays")
	val repeatDays: List<String?>? = null,

	@field:SerializedName("exercises")
	val exercises: List<ExercisesItem?>? = null,

	@field:SerializedName("notificationTime")
	val notificationTime: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("userId")
	val userId: String? = null
)
