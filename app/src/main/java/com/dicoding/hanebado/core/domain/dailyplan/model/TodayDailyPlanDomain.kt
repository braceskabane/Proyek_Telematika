package com.dicoding.hanebado.core.domain.dailyplan.model

data class TodayDailyPlanDomain(
    val id: Int,
    val label: String,
    val notificationTime: String,
    val repeatDays: List<String>,
    val exercises: List<TodayExerciseDomain>,
    val isActive: Boolean,
    val userId: String,
    val createdAt: String
)

data class TodayExerciseDomain(
    val id: Int,
    val dailyPlanId: Int,
    val exerciseId: Int, // Tambahkan parameter ini
    val reps: Int,
    val sets: Int,
    val order: Int,
    val isCompleted: Boolean,
    val exercise: TodayExerciseDetailDomain
)

data class TodayExerciseDetailDomain(
    val id: Int,
    val name: String,
    val description: String,
    val difficultyXP: Int,
    val createdAt: String,
    val updatedAt: String
)