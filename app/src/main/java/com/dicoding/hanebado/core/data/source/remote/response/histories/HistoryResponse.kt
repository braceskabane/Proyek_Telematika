package com.dicoding.hanebado.core.data.source.remote.response.histories

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
	@SerializedName("data")
	val data: List<Any?>? = null,

	@SerializedName("success")
	val success: Boolean? = null,

	@SerializedName("meta")
	val meta: Meta? = null
)

data class Pagination(
	@SerializedName("total")
	val total: Int? = null,

	@SerializedName("has_next_page")
	val hasNextPage: Boolean? = null,

	@SerializedName("limit")
	val limit: Int? = null,

	@SerializedName("total_pages")
	val totalPages: Int? = null,

	@SerializedName("has_previous_page")
	val hasPreviousPage: Boolean? = null,

	@SerializedName("page")
	val page: Int? = null
)


data class Meta(
	@SerializedName("pagination")
	val pagination: Pagination? = null,

	@SerializedName("stats")
	val stats: Stats? = null
)

data class Stats(
	@SerializedName("total_duration")
	val totalDuration: Int? = null,

	@SerializedName("total_sessions")
	val totalSessions: Int? = null,

	@SerializedName("in_progress")
	val inProgress: Int? = null,

	@SerializedName("uncompleted")
	val uncompleted: Int? = null,

	@SerializedName("completed")
	val completed: Int? = null,

	@SerializedName("average_duration")
	val averageDuration: Int? = null
)
