package com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.domain.exercise.model.Exercise
import com.dicoding.hanebado.core.domain.exercise.model.SelectedExerciseData
import com.dicoding.hanebado.databinding.ItemDailyAddWorkoutCustomBinding

class ExerciseAdapter : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    private val exercises = mutableListOf<Exercise>()
    private val exerciseInputs = mutableMapOf<Int, Pair<Int, Int>>() // exerciseId to (sets, reps)

    fun submitList(list: List<Exercise>) {
        exercises.clear()
        exercises.addAll(list)
        notifyDataSetChanged()
    }

    fun getSelectedExercises(): List<SelectedExerciseData> {
        return exerciseInputs.entries
            .filter { entry ->
                // Filter untuk nilai > 0
                entry.value.first > 0 && entry.value.second > 0
            }
            .mapIndexed { index, entry ->
                SelectedExerciseData(
                    exerciseId = entry.key,
                    sets = entry.value.first,
                    reps = entry.value.second,
                    order = index + 1
                )
            }
    }

    inner class ExerciseViewHolder(
        private val binding: ItemDailyAddWorkoutCustomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: Exercise) {
            with(binding) {
                // Tampilkan nama exercise dari endpoint
                tvPushUp.text = exercise.name

                // Ambil data input yang sudah ada (jika ada)
                val currentInput = exerciseInputs[exercise.id]
                etSetPushUp.setText(currentInput?.first?.toString() ?: "")
                etRepetitionsPushUp.setText(currentInput?.second?.toString() ?: "")

                // Handle input sets
                etSetPushUp.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        updateInput(exercise.id, s?.toString()?.toIntOrNull() ?: 0, isSet = true)
                    }
                })

                // Handle input repetitions
                etRepetitionsPushUp.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        updateInput(exercise.id, s?.toString()?.toIntOrNull() ?: 0, isSet = false)
                    }
                })

                // Update icon status
                updateExerciseStatus(exercise.id)
            }
        }

        private fun updateInput(exerciseId: Int, value: Int, isSet: Boolean) {
            val currentPair = exerciseInputs[exerciseId] ?: Pair(0, 0)
            val newPair = if (isSet) {
                Pair(value, currentPair.second)
            } else {
                Pair(currentPair.first, value)
            }

            if (newPair.first > 0 || newPair.second > 0) {
                exerciseInputs[exerciseId] = newPair
            } else {
                exerciseInputs.remove(exerciseId)
            }

            updateExerciseStatus(exerciseId)
        }

        private fun updateExerciseStatus(exerciseId: Int) {
            val input = exerciseInputs[exerciseId]
            binding.ivStatusPushUp.visibility = if (input?.first ?: 0 > 0 && input?.second ?: 0 > 0) {
                binding.ivStatusPushUp.setImageResource(R.drawable.icon_done)
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(
            ItemDailyAddWorkoutCustomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount() = exercises.size
}