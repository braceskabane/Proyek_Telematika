package com.dicoding.hanebado.core.domain.exercise.usecase

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseUseCase {
    fun getAllExercises(): Flow<Resource<List<Exercise>>>
}