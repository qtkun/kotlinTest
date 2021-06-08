package com.qtk.kotlintest.utils

import java.text.SimpleDateFormat
import java.util.*

data class CalendarBean(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekday: Int,
    val dateString: String,
    val millisecond: Long,
    val type: Int, //0 上个月, 1这个月，2下个月
    var select: Boolean = false,
)

fun monthCount(): Int = 20 * 12

fun positionToDate(position: Int): IntArray {
    val calendar = Calendar.getInstance()
    val nowYear = calendar[Calendar.YEAR]
    val startY = nowYear - 10
    val year = position / 12 + startY
    val month = position % 12 + 1
    return intArrayOf(year, month)
}

fun dateToPosition(): Int {
    val calendar = Calendar.getInstance()
    val month = calendar[Calendar.MONTH] + 1
    return 10 * 12 + month - 1
}

fun getMonthDate(date: IntArray): List<CalendarBean> {
    val days = arrayListOf<CalendarBean>()
    val year = date[0]
    val month = date[1]
    val week = getFirstWeekOfMonth(year, month - 1)

    val lastYear: Int
    val lastMonth: Int
    if (month == 1) {
        lastMonth = 12
        lastYear = year - 1
    } else {
        lastMonth = month - 1
        lastYear = year
    }
    val lastMonthDays = getMonthDays(lastYear, lastMonth)
    val currentMonthDays = getMonthDays(year, month)

    val nextYear: Int
    val nextMonth: Int
    if (month == 12) {
        nextMonth = 1
        nextYear = year + 1
    } else {
        nextMonth = month + 1
        nextYear = year
    }
    for (i in 0 until week) {
        days.add(initCalendarBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, 0))
    }
    for (i in 0 until currentMonthDays) {
        days.add(initCalendarBean(year, month, i + 1, 1))
    }
    for (i in 0 until 42 - currentMonthDays - week) {
        days.add(initCalendarBean(nextYear, nextMonth, i + 1, 2))
    }
    return days
}

fun initCalendarBean(year: Int, month: Int, day: Int, type: Int): CalendarBean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance().apply { set(year, month - 1, day) }
    val dateString = sdf.format(calendar.time)
    return CalendarBean(
        year, month, day, calendar[Calendar.DAY_OF_WEEK] - 1,
        dateString, calendar.timeInMillis, type
    )
}

fun getFirstWeekOfMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar[Calendar.DAY_OF_WEEK] - 1
}

fun getMonthDays(year: Int, month: Int): Int = when(month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            29
        } else {
            28
        }
    }
    else -> -1
}

fun getMonthRows(year: Int, month: Int): Int {
    val items: Int = getFirstWeekOfMonth(year, month - 1) + getMonthDays(year, month)
    return if (items % 7 == 0) items / 7 else items / 7 + 1
}