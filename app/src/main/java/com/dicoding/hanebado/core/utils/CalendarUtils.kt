package com.dicoding.hanebado.core.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CalendarUtils {
    var selectedDate: Calendar = Calendar.getInstance()

    fun formattedDate(date: Calendar): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(date.time)
    }

    fun formattedTime(time: Calendar): String {
        val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return formatter.format(time.time)
    }

    fun monthYearFromDate(date: Calendar): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return formatter.format(date.time)
    }

    fun daysInMonthArray(date: Calendar): ArrayList<Calendar?> {
        val daysInMonthArray = ArrayList<Calendar?>()
        val yearMonth = date.clone() as Calendar
        yearMonth.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = yearMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        val firstOfMonth = (selectedDate.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val dayOfWeek = firstOfMonth.get(Calendar.DAY_OF_WEEK) - 1

        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(null)
            else {
                val calendarDay = Calendar.getInstance()
                calendarDay.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), i - dayOfWeek)
                daysInMonthArray.add(calendarDay)
            }
        }
        return daysInMonthArray
    }

    fun daysInWeekArray(date: Calendar): ArrayList<Calendar?> {
        Log.d("CalendarUtils", "daysInWeekArray called with date: ${date.time}")
        val days = ArrayList<Calendar?>()
        val currentDay = date.clone() as Calendar
        currentDay.set(Calendar.DAY_OF_WEEK, currentDay.firstDayOfWeek)

        for (i in 0 until 7) {
            days.add(currentDay.clone() as Calendar)
            currentDay.add(Calendar.DAY_OF_MONTH, 1)
        }

        Log.d("CalendarUtils", "Returned days: ${days.joinToString { it?.time.toString() }}")
        return days
    }

    private fun sundayForDate(current: Calendar): Calendar {
        val oneWeekAgo = (current.clone() as Calendar).apply { add(Calendar.WEEK_OF_YEAR, -1) }
        val mutableCurrent = current.clone() as Calendar

        while (mutableCurrent.after(oneWeekAgo)) {
            if (mutableCurrent.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                return mutableCurrent

            mutableCurrent.add(Calendar.DAY_OF_MONTH, -1)
        }

        throw IllegalStateException("Sunday not found")
    }

    fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        if (cal1 == null || cal2 == null) return false
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}