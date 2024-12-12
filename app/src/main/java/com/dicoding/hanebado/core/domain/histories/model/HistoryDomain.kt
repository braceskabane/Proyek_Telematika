package com.dicoding.hanebado.core.domain.histories.model

data class HistoryDomain(
    val id: String?,
    val totalDuration: Int?,
    val totalSessions: Int?,
    val inProgress: Int?,
    val uncompleted: Int?,
    val completed: Int?,
    val averageDuration: Int?,
    val page: Int?,
    val limit: Int?,
    val totalPages: Int?,
    val hasNextPage: Boolean?,
    val hasPreviousPage: Boolean?
)