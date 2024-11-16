package com.dicoding.hanebado.view.dashboard.plan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.data.source.Resource
import com.dicoding.hanebado.core.ui.adapter.CalendarAdapter
import com.dicoding.hanebado.core.utils.CalendarUtils
import com.dicoding.hanebado.databinding.FragmentDailyPlanBinding
import com.dicoding.hanebado.view.dashboard.plan.addworkout.AddWorkoutDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class DailyPlanFragment : Fragment(), CalendarAdapter.OnItemListener, DailyPlanRefreshListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventListRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private var _binding: FragmentDailyPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressAdapter: DailyPlanProgressAdapter
    private val viewModel: DailyPlanViewModel by viewModels()

    override fun onDailyPlanAdded() {
        Log.d("DailyPlanFragment", "Received callback to refresh data")
        viewModel.getAllDailyPlans() // Refresh data
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("DailyPlanFragment", "onCreateView called")
        _binding = FragmentDailyPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DailyPlanFragment", "onViewCreated called")
//        initWidgets(view)
//        CalendarUtils.selectedDate = Calendar.getInstance()
//        setupRecyclerViews()

        // Setup result listener
        parentFragmentManager.setFragmentResultListener("workout_added", viewLifecycleOwner) { _, _ ->
            Log.d("DailyPlanFragment", "Workout added, refreshing data...")
            viewModel.getAllDailyPlans()
        }

        try {
            CalendarUtils.selectedDate = Calendar.getInstance()
            setupRecyclerViews()
            setupButtons()
            observeDailyPlans()
        } catch (e: Exception) {
            Log.e("DailyPlanFragment", "Error in onViewCreated: ${e.message}", e)
        }

    }
    private fun setupButtons() {
        try {
            with(binding) {
                previousWeekButton.setOnClickListener { previousWeekAction() }
                nextWeekButton.setOnClickListener { nextWeekAction() }
                btnAddWorkout.setOnClickListener { showAddWorkoutDialog() }
            }
        } catch (e: Exception) {
            Log.e("DailyPlanFragment", "Error in setupButtons: ${e.message}", e)
        }
    }

    private fun observeDailyPlans() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyPlans.collect { result ->
                Log.d("DailyPlanFragment", "Received daily plans update: $result")
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { dailyPlans ->
                            Log.d("DailyPlanFragment", "Updating adapter with ${dailyPlans.data.size} items")
                            progressAdapter.submitList(dailyPlans.data)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(
                            context,
                            result.message ?: "Error fetching daily plans",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Message -> {
                        // Handle message
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showAddWorkoutDialog() {
        val dialog = AddWorkoutDialog().apply {
            refreshListener = this@DailyPlanFragment  // Set listener
        }
        dialog.show(childFragmentManager, AddWorkoutDialog.TAG)
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.rvCalendar)
        monthYearText = view.findViewById(R.id.monthYearTV)
        eventListRecyclerView = view.findViewById(R.id.rv_event_list_view)
    }

    private fun setupRecyclerViews() {
        try {
            // Setup Calendar RecyclerView
            calendarAdapter = CalendarAdapter(ArrayList(), this)
            binding.rvCalendar.let { calendar ->
                calendar.layoutManager = GridLayoutManager(requireContext(), 7)
                calendar.adapter = calendarAdapter
            } ?: Log.e("DailyPlanFragment", "rvCalendar is null")

            setWeekView()

            // Setup Progress RecyclerView
            progressAdapter = DailyPlanProgressAdapter()
            binding.rvEventListView.let { eventList ->
                eventList.layoutManager = LinearLayoutManager(context)
                eventList.adapter = progressAdapter
            } ?: Log.e("DailyPlanFragment", "rvEventListView is null")

        } catch (e: Exception) {
            Log.e("DailyPlanFragment", "Error in setupRecyclerViews: ${e.message}", e)
        }
    }

    private fun setWeekView() {
        try {
            binding.monthYearTV.text = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
            val days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate)
            calendarAdapter.updateData(days)
        } catch (e: Exception) {
            Log.e("DailyPlanFragment", "Error in setWeekView: ${e.message}", e)
        }
    }

    private fun previousWeekAction() {
        CalendarUtils.selectedDate.add(Calendar.WEEK_OF_YEAR, -1)
        setWeekView()
    }

    private fun nextWeekAction() {
        CalendarUtils.selectedDate.add(Calendar.WEEK_OF_YEAR, 1)
        setWeekView()
    }

    override fun onItemClick(position: Int, date: Calendar?) {
        date?.let {
            CalendarUtils.selectedDate = it
            setWeekView()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DailyPlanFragment", "onResume called")
        setWeekView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Bersihkan binding saat view destroyed
    }
}