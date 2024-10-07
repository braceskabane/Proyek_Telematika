package com.dicoding.hanebado.view.dashboard.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.hanebado.view.dashboard.history.reflex.HistoryReflexFragment
import com.dicoding.hanebado.view.dashboard.history.workout.HistoryWorkoutFragment

class HistoryPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryWorkoutFragment()
            1 -> HistoryReflexFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}