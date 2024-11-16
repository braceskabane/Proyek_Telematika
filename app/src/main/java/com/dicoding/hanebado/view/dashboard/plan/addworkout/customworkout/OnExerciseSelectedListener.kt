package com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout

import com.dicoding.hanebado.core.domain.exercise.model.SelectedExerciseData

interface OnExerciseSelectedListener {
    fun onExerciseSelected(exercises: List<SelectedExerciseData>)
}