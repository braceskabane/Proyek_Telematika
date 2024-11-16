package com.dicoding.hanebado.core.domain.dailyplan.model

data class DailyPlanListDomain(
    val data: List<DailyPlanItemDomain>,
    val meta: MetaDomain
)

data class DailyPlanItemDomain(
    val id: Int,
    val label: String,
    val notificationTime: String,
    val repeatDays: List<String>,
    val exercises: List<DailyPlanExerciseDomain>,
    val isActive: Boolean,
    val userId: String,
    val createdAt: String
)

data class DailyPlanExerciseDomain(
    val id: Int,
    val dailyPlanId: Int,
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val order: Int,
    val isCompleted: Boolean,
    val exercise: ExerciseDomain
)

data class MetaDomain(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

data class ExerciseDomain(
    val id: Int,
    val name: String,
    val description: String,
    val difficultyXP: Int,
    val createdAt: String,
    val updatedAt: String
)