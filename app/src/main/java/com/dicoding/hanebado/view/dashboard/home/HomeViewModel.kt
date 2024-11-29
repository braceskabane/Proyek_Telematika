package com.dicoding.hanebado.view.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.usecase.DailyPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyPlanUseCase: DailyPlanUseCase
) : ViewModel() {

    private val _todayPlan = MutableStateFlow<Resource<List<TodayDailyPlanDomain>>>(Resource.Loading())
    val todayPlan = _todayPlan.asStateFlow()

    init {
        getTodayPlan()
    }

    fun getTodayPlan() {
        viewModelScope.launch {
            dailyPlanUseCase.getTodayDailyPlan()
                .collect { result ->
                    _todayPlan.value = result
                }
        }
    }
}