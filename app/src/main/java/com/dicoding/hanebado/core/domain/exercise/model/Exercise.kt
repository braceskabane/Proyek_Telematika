package com.dicoding.hanebado.core.domain.exercise.model

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val difficultyXP: Int,
    val createdAt: String,
    val updatedAt: String
)