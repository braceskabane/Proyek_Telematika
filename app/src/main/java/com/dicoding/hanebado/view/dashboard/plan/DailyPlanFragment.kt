package com.dicoding.hanebado.view.dashboard.plan

import AddWorkoutDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R
import com.dicoding.hanebado.core.ui.adapter.CalendarAdapter
import com.dicoding.hanebado.core.utils.CalendarUtils
import java.util.Calendar

class DailyPlanFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventListRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("DailyPlanFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_daily_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DailyPlanFragment", "onViewCreated called")
        initWidgets(view)
        CalendarUtils.selectedDate = Calendar.getInstance()
        setupRecyclerView()

        view.findViewById<Button>(R.id.previousWeekButton).setOnClickListener { previousWeekAction() }
        view.findViewById<Button>(R.id.nextWeekButton).setOnClickListener { nextWeekAction() }
        view.findViewById<Button>(R.id.btn_AddWorkout).setOnClickListener { showAddWorkoutDialog() }
    }

    private fun showAddWorkoutDialog() {
        val dialog = AddWorkoutDialog()
        dialog.show(childFragmentManager, "AddWorkoutDialog")
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.rvCalendar)
        monthYearText = view.findViewById(R.id.monthYearTV)
        eventListRecyclerView = view.findViewById(R.id.rvEventListView)
    }

    private fun setupRecyclerView() {
        Log.d("DailyPlanFragment", "setupRecyclerView called")
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarAdapter = CalendarAdapter(ArrayList(), this)
        calendarRecyclerView.adapter = calendarAdapter
        setWeekView()
    }

    private fun setWeekView() {
        Log.d("DailyPlanFragment", "setWeekView called")
        monthYearText.text = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate)
        val days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate)
        Log.d("DailyPlanFragment", "Days: ${days.joinToString { it?.time.toString() }}")
        calendarAdapter.updateData(days)
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
}