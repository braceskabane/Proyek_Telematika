package com.dicoding.hanebado.view.dashboard.plan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanListDomain
import com.dicoding.hanebado.core.domain.dailyplan.usecase.DailyPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyPlanViewModel @Inject constructor(
    private val dailyPlanUseCase: DailyPlanUseCase
) : ViewModel() {

    private val _dailyPlans = MutableStateFlow<Resource<DailyPlanListDomain>>(Resource.Loading())
    val dailyPlans: StateFlow<Resource<DailyPlanListDomain>> = _dailyPlans.asStateFlow()

    init {
        getAllDailyPlans()
    }

    fun getAllDailyPlans() {
        Log.d("DailyPlanViewModel", "Fetching updated daily plans")
        viewModelScope.launch {
            try {
                dailyPlanUseCase.getAllDailyPlan()
                    .collect { result ->
                        Log.d("DailyPlanViewModel", "Received new data: $result")
                        _dailyPlans.value = result
                    }
            } catch (e: Exception) {
                Log.e("DailyPlanViewModel", "Error fetching daily plans", e)
                _dailyPlans.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}