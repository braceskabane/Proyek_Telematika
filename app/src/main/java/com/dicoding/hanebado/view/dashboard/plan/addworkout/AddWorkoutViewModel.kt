package com.dicoding.hanebado.view.dashboard.plan.addworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.DailyPlanRequest
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.ExerciseRequest
import com.dicoding.hanebado.core.domain.auth.usecase.AuthUseCase
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.usecase.DailyPlanUseCase
import com.dicoding.hanebado.core.domain.exercise.model.SelectedExerciseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    private val dailyPlanUseCase: DailyPlanUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _addDailyPlanResult = MutableStateFlow<Resource<DailyPlanDomain>>(Resource.Loading())
    val addDailyPlanResult: StateFlow<Resource<DailyPlanDomain>> = _addDailyPlanResult.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            authUseCase.getLoginStatus().collect { isLoggedIn ->
                _isAuthenticated.value = isLoggedIn
            }
        }
    }

    fun refreshAuthStatus() {
        checkAuthentication()
    }

    fun addDailyPlan(
        label: String,
        selectedDays: List<String>,
        selectedExercises: List<SelectedExerciseData>
    ) {
        viewModelScope.launch {
            try {
                _addDailyPlanResult.value = Resource.Loading()

                dailyPlanUseCase.addDailyPlan(
                    DailyPlanRequest(
                        notificationTime = "2024-11-11T06:00:00Z",
                        repeatDays = selectedDays,
                        label = label,
                        exercises = selectedExercises.map { exercise ->
                            ExerciseRequest(
                                exerciseId = exercise.exerciseId,
                                sets = exercise.sets,
                                reps = exercise.reps,
                                order = exercise.order
                            )
                        }
                    )
                ).collect { result ->
                    _addDailyPlanResult.value = result
                }
            } catch (e: Exception) {
                _addDailyPlanResult.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}