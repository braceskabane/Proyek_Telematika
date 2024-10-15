package com.dicoding.hanebado.view.dashboard.plan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.hanebado.R
import java.util.Calendar

class CalendarAdapter(
    private var days: ArrayList<Calendar?>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarViewHolder>() {

    fun updateData(newDays: ArrayList<Calendar?>) {
        Log.d("CalendarAdapter", "updateData called with ${newDays.size} days")
        days = newDays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        Log.d("CalendarAdapter", "onCreateViewHolder called")
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_weeklyplan_calendar, parent, false)
        return CalendarViewHolder(view, onItemListener, days)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Log.d("CalendarAdapter", "onBindViewHolder called for position $position")
        val date = days[position]
        if (date == null) {
            holder.dayOfMonth.text = ""
        } else {
            holder.dayOfMonth.text = date.get(Calendar.DAY_OF_MONTH).toString()
            if (CalendarUtils.isSameDay(date, CalendarUtils.selectedDate)) {
                holder.parentView.setBackgroundResource(R.drawable.custom_background_home_daily_plan)
            } else {
                holder.parentView.background = null
            }
        }
    }

    override fun getItemCount(): Int = days.size

    interface OnItemListener {
        fun onItemClick(position: Int, date: Calendar?)
    }
}