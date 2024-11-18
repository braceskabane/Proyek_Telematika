package com.dicoding.hanebado.core.data.source.remote.response.dailyplan

import com.google.gson.annotations.SerializedName

data class GetTodayResponse(

	@field:SerializedName("data")
	val data: TodayData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class TodayData(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("repeatDays")
	val repeatDays: List<String?>? = null,

	@field:SerializedName("exercises")
	val exercises: List<TodayExercisesItem?>? = null,

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

data class TodayExercise(

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

data class TodayExercisesItem(

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
	val exercise: TodayExercise? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order")
	val order: Int? = null,

	@field:SerializedName("isCompleted")
	val isCompleted: Boolean? = null
)
