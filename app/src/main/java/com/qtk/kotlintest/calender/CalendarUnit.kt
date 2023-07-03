package com.qtk.kotlintest.calender

import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.calender.adapter.CalenderSelectAdapter
import com.qtk.kotlintest.calender.adapter.TimeSelectAdapter
import com.qtk.kotlintest.databinding.DialogCalenderBinding
import com.qtk.kotlintest.databinding.DialogDateBinding
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

data class CalendarBean(
    val year: Int,
    val month: Int,
    val day: Int,
    val weekday: Int,
    val dateString: String,
    val millisecond: Long,
    val type: MonthType,
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

fun dateToPosition(year: Int, month: Int): Int {
    val startY = Calendar.getInstance()[Calendar.YEAR] - 10
    return (year - startY) * 12 + month - 1
}

fun getMonthDate(date: IntArray, initialDate: IntArray): List<CalendarBean> {
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
        days.add(initCalendarBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, MonthType.PREVIOUS, initialDate))
    }
    for (i in 0 until currentMonthDays) {
        days.add(initCalendarBean(year, month, i + 1, MonthType.CURRENT, initialDate))
    }
    for (i in 0 until 42 - currentMonthDays - week) {
        days.add(initCalendarBean(nextYear, nextMonth, i + 1, MonthType.NEXT, initialDate))
    }
    return days
}

fun initCalendarBean(year: Int, month: Int, day: Int, type: MonthType, initialDate: IntArray): CalendarBean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance().apply { set(year, month - 1, day) }
    val dateString = sdf.format(calendar.time)
    return CalendarBean(
        year, month, day, calendar[Calendar.DAY_OF_WEEK] - 1,
        dateString, calendar.timeInMillis, type,
        initialDate[0] == year && initialDate[1] == month && initialDate[2] == day
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

fun String.isToday(): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val date = sdf.parse(this)
        date?.let {
            val now = Date()
            if (TextUtils.equals(sdf2.format(date), sdf2.format(now))) {
                return true
            }
        }
    } catch (e: Exception) {
        try {
            val date = sdf2.parse(this)
            date?.let {
                val now = Date()
                if (TextUtils.equals(sdf2.format(date), sdf2.format(now))) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
    }
    return false
}

fun String.earlierThanToday(): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf2.parse(this)
    val now = Date().let {
        sdf2.parse(sdf.format(it))
    }
    date?.let { d ->
        now?.let { n ->
            if (d.time < n.time) {
                return true
            }
        }
    }
    return false
}

fun getToday(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun String.getYearAndMonth(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val sdf2 = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    val date = sdf.parse(this)
    return sdf2.format(date ?: "")
}

fun String.getHourAndMinutes(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val sdf2 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = sdf.parse(this)
    return sdf2.format(date ?: "")
}

fun FragmentActivity.initDateTimeBottomSheet(
    initTime: String?,
    onDaySelect: (String) -> Unit,
    onTimeSelect: (String) -> Unit,
    onConfirm: () -> Unit,
    canSelectBeforeToday: Boolean = true
): BottomSheetDialog {
    val binding = DialogCalenderBinding.inflate(layoutInflater)
    val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
        setOnDismissListener {
            binding.tabLayout.getTabAt(0)?.select()
        }
    }
    with(binding) {
        calendarPager.adapter = ConcatAdapter(
            CalenderSelectAdapter(
                initTime ?: "",
                onMonthSelect = {
                    tabLayout.getTabAt(0)?.text = it
                },
                onDaySelect = {
                    onDaySelect(it.dateString)
                    tabLayout.getTabAt(1)?.select()
                },
                canSelectBeforeToday
            ),
            TimeSelectAdapter(
                initTime ?: "",
                onTimeSelect = {
                    onTimeSelect(it)
                    tabLayout.getTabAt(1)?.text = it
                }
            )
        )
        TabLayoutMediator(
            tabLayout, calendarPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = initTime?.getYearAndMonth()
                }
                1 -> {
                    tab.text = initTime?.getHourAndMinutes()
                }
            }
        }.attach()
        confirm.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }
    }
    dialog.setContentView(binding.root)
    return dialog
}

fun FragmentActivity.initDateBottomSheet(
    initTime: String,
    onConfirm: (String) -> Unit,
    canSelectBeforeToday: Boolean = true
): BottomSheetDialog {
    val binding = DialogDateBinding.inflate(layoutInflater)
    val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
        window?.run {
            setDimAmount(0.2f)
        }
    }

    var date = initTime
    with(binding) {
        yearMonth.text = initTime.getYearAndMonth()
        calendarPager.adapter = CalenderSelectAdapter(
            initTime,
            onMonthSelect = {
                yearMonth.text = it
            },
            onDaySelect = {
                date = it.dateString
            },
            canSelectBeforeToday
        )
        confirm.setOnClickListener {
            dialog.dismiss()
            onConfirm(date)
        }
    }
    dialog.setContentView(binding.root)
    return dialog
}