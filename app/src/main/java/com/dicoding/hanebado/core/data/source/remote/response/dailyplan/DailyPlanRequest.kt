package com.dicoding.hanebado.core.data.source.remote.response.dailyplan

data class DailyPlanRequest(
    val repeatDays: List<String>,
    val label: String,
    val exercises: List<ExerciseRequest>
)

data class ExerciseRequest(
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val order: Int
)
