package com.dicoding.hanebado.view.dashboard.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.exercise.model.SessionDomain
import com.dicoding.hanebado.core.domain.exercise.usecase.ExerciseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val exerciseUseCase: ExerciseUseCase
) : ViewModel() {

    private val _sessionDomainState = MutableStateFlow<Resource<SessionDomain>>(Resource.Loading())
    val sessionDomainState: StateFlow<Resource<SessionDomain>> = _sessionDomainState.asStateFlow()

    fun startExerciseSession(dailyPlanId: Int, exerciseId: Int, setNumber: Int, reps: Int, duration: Int) {
        viewModelScope.launch {
            exerciseUseCase.saveExerciseSession(dailyPlanId, exerciseId, setNumber, reps, duration)
                .collect { result ->
                    _sessionDomainState.value = result
                }
        }
    }
}