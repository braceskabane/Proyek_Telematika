package com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import com.dicoding.hanebado.core.domain.exercise.usecase.ExerciseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseUseCase: ExerciseUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _exercises = MutableStateFlow<Resource<List<Exercise>>>(Resource.Loading())
    val exercises: StateFlow<Resource<List<Exercise>>> = _exercises.asStateFlow()

    init {
        getExercises()
    }

    fun getExercises() {
        viewModelScope.launch {
            exerciseUseCase.getAllExercises().collect { result ->
                _exercises.value = result
            }
        }
    }
}