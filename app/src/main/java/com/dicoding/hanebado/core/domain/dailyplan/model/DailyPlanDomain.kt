package com.dicoding.hanebado.core.domain.dailyplan.model

import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.Exercise

data class DailyPlanDomain(
    val id: Int,
    val label: String,
    val userId: String,
    val isActive: Boolean,
    val createdAt: String,
    val notificationTime: String?,
    val repeatDays: List<String>,
    val exercises: List<ExerciseItem>
) {
    data class ExerciseItem(
        val id: Int,
        val dailyPlanId: Int,
        val reps: Int,
        val sets: Int,
        val order: Int,
        val isCompleted: Boolean,
        val exercise: Exercise
    )

    data class Exercise(
        val id: Int,
        val name: String,
        val description: String,
        val createdAt: String,
        val updatedAt: String,
        val difficultyXP: Int
    )
}