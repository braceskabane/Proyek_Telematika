package com.dicoding.hanebado.core.domain.exercise.repository

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import kotlinx.coroutines.flow.Flow

interface IExerciseRepository {
    fun getAllExercises(): Flow<Resource<List<Exercise>>>
}