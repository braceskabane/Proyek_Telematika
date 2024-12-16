package com.dicoding.hanebado.core.domain.exercise.interactor

import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import com.dicoding.hanebado.core.domain.exercise.model.SessionDomain
import com.dicoding.hanebado.core.domain.exercise.repository.IExerciseRepository
import com.dicoding.hanebado.core.domain.exercise.usecase.ExerciseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseInteractor @Inject constructor(
    private val exerciseRepository: IExerciseRepository
) : ExerciseUseCase {
    override fun getAllExercises(): Flow<Resource<List<Exercise>>> {
        return exerciseRepository.getAllExercises()
    }
    override fun saveExerciseSession(dailyPlanId: Int, exerciseId: Int, setNumber: Int, reps: Int, duration: Int): Flow<Resource<SessionDomain>> {
        return exerciseRepository.saveExerciseSession(dailyPlanId, exerciseId, setNumber, reps, duration)
    }
}