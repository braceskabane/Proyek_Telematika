package com.dicoding.hanebado.core.domain.exercise.model

data class SelectedExerciseData(
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val order: Int
)