package com.dicoding.hanebado.core.domain.exercise.repository

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import com.dicoding.hanebado.core.domain.exercise.model.SessionDomain
import kotlinx.coroutines.flow.Flow

interface IExerciseRepository {
    fun getAllExercises(): Flow<Resource<List<Exercise>>>
    fun saveExerciseSession(dailyPlanId: Int, exerciseId: Int, setNumber: Int, reps: Int, duration: Int): Flow<Resource<SessionDomain>>
}