package com.dicoding.core.utils.constants

import com.dicoding.core.utils.DateUtils

enum class FilterDate(val value: String) {
    NOW(DateUtils.getDateThisDay()),
    MONTH(DateUtils.getDateThisMonth()),
    YEAR(DateUtils.getDateThisYear())
}

//val filterDateList =
//    listOf(FilterDate.NOW.display, FilterDate.MONTH.display, FilterDate.YEAR.display)
//
//fun mapToFilterDateValue(display: String): String {
//    return when (display) {
//        FilterDate.NOW.display -> {
//            FilterDate.NOW.value
//        }
//        FilterDate.MONTH.display -> {
//            FilterDate.MONTH.value
//        }
//        FilterDate.YEAR.display -> {
//            FilterDate.YEAR.value
//        }
//        else -> {
//            FilterDate.NOW.value
//        }
//    }
//}