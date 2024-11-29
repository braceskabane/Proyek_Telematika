package com.dicoding.hanebado.view.dashboard.record.dialogplan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain
import com.dicoding.hanebado.databinding.ItemTodayPlanDialogReadyBinding

class DailyPlanAdapter(private val onExerciseClickListener: (TodayExerciseDomain) -> Unit,
                       private val onReadyClickListener: (TodayExerciseDomain) -> Unit) :
    RecyclerView.Adapter<DailyPlanAdapter.DailyPlanViewHolder>() {

    private val dailyPlans = mutableListOf<TodayDailyPlanDomain>()

    fun setDailyPlans(plans: List<TodayDailyPlanDomain>) {
        dailyPlans.clear()
        dailyPlans.addAll(plans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyPlanViewHolder {
        val binding = ItemTodayPlanDialogReadyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyPlanViewHolder, position: Int) {
        val plan = dailyPlans[position]
        holder.bind(plan)
    }

    override fun getItemCount(): Int = dailyPlans.size

    inner class DailyPlanViewHolder(private val binding: ItemTodayPlanDialogReadyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: TodayDailyPlanDomain) {
            // Setup the nested RecyclerView for exercises
            val exerciseAdapter = ExerciseAdapter(
                onExerciseClickListener = onExerciseClickListener,
                onReadyClickListener = onReadyClickListener
            )
            binding.rvSportCategoryItem.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvSportCategoryItem.adapter = exerciseAdapter
            exerciseAdapter.setExercises(plan.exercises)
        }
    }
}

