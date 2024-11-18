package com.dicoding.hanebado.view.dashboard.record

import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain

interface OnTodayExerciseSelectedListener {
    fun onExerciseSelected(exercise: TodayExerciseDomain)
}
