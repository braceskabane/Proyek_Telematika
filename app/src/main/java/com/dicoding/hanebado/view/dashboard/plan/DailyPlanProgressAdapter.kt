package com.dicoding.hanebado.view.dashboard.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanItemDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanProgressItem
import com.dicoding.hanebado.databinding.ItemProgressWorkoutBinding

class DailyPlanProgressAdapter : RecyclerView.Adapter<DailyPlanProgressAdapter.ProgressViewHolder>() {
    private val items = mutableListOf<DailyPlanProgressItem>()

    fun submitList(dailyPlans: List<DailyPlanItemDomain>) {
        items.clear()
        items.addAll(dailyPlans.map { plan ->
            DailyPlanProgressItem(
                id = plan.id,
                label = plan.label,
                exerciseCount = plan.exercises.size
            )
        })
        notifyDataSetChanged()
    }

    inner class ProgressViewHolder(
        private val binding: ItemProgressWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DailyPlanProgressItem) {
            with(binding) {
                tvDailyWorkoutProgress.text = item.label
                tvExerciseRemainder.text = "${item.exerciseCount} Exercise left"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        return ProgressViewHolder(
            ItemProgressWorkoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}