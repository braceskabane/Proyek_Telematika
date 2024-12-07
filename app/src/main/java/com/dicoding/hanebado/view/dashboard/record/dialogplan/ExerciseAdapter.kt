package com.dicoding.hanebado.view.dashboard.record.dialogplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain
import com.dicoding.hanebado.databinding.ItemRecordDailyWorkoutBinding

class ExerciseAdapter(
    private val onReadyClickListener: ((TodayExerciseDomain) -> Unit)? = null // Add this line
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val exercises = mutableListOf<TodayExerciseDomain>()

    fun setExercises(exerciseList: List<TodayExerciseDomain>) {
        exercises.clear()
        exercises.addAll(exerciseList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemRecordDailyWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(private val binding: ItemRecordDailyWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: TodayExerciseDomain) {
            with(binding) {
                // Set nama exercise
                tvCategorizeMotion.text = exercise.exercise.name

                // Set repetisi dan set
                tvRepetisiAndSet.text = "${exercise.sets}×${exercise.reps}"

                // Set status button
                ivDone.apply {
                    text = if (exercise.isCompleted) "Done" else "Ready"
                    isEnabled = !exercise.isCompleted
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            if (exercise.isCompleted) R.color.anticipation else R.color.red
                        )
                    )
                    setOnClickListener {
                        if (!exercise.isCompleted) {
                            onReadyClickListener?.invoke(exercise) // Trigger the listener if it's not completed
                        }
                    }
                }

                // Optional: Set icon berdasarkan jenis exercise
                circleImageView.setImageResource(
                    when (exercise.exercise.name.lowercase()) {
                        "Push-up" -> R.drawable.icon_push_up
                        "Sit-up" -> R.drawable.icon_sit_up
                        "Squat" -> R.drawable.icon_squat
                        "Pull-up" -> R.drawable.icon_pull_up
                        "Plank" -> R.drawable.icon_plank
                        "Jump Rope" -> R.drawable.icon_jump_rope
                        else -> R.drawable.icon_push_up // default icon
                    }
                )
            }
        }
    }
}
