package com.qtk.kotlintest.calender.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.calender.CalendarBean
import com.qtk.kotlintest.calender.MonthType
import com.qtk.kotlintest.calender.dateToPosition
import com.qtk.kotlintest.databinding.CalenderLayoutBinding
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CalenderSelectAdapter(
    initTime: String,
    private val onMonthSelect: (String) -> Unit,
    private val onDaySelect: (CalendarBean) -> Unit,
    private val canSelectBeforeToday: Boolean
): RecyclerView.Adapter<CalenderSelectAdapter.CalenderViewHolder>() {
    private var date = intArrayOf()

    init {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calender = Calendar.getInstance()
        try {
            sdf.parse(initTime)?.let {
                calender.time = it
            }
        }catch (ignore: Exception){}
        date = intArrayOf(
            calender[Calendar.YEAR],
            calender[Calendar.MONTH] + 1,
            calender[Calendar.DAY_OF_MONTH]
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {
        val binding = CalenderLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalenderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int = 1

    inner class CalenderViewHolder(private val binding: CalenderLayoutBinding): RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bindView() {
            with(binding) {
                PagerSnapHelper().attachToRecyclerView(calendarRv)
                val calendarPagerAdapter = CalendarAdapter(date, calendarRv, canSelectBeforeToday, onDaySelect)
                calendarRv.adapter = calendarPagerAdapter
                calendarRv.layoutManager = LinearLayoutManager(root.context)
                if (date.size == 2) {
                    calendarRv.scrollToPosition(dateToPosition(date[0], date[1]))
                } else {
                    calendarRv.scrollToPosition(dateToPosition(date[0], date[1]))
                }
                calendarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val firstIndex =
                            (calendarRv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        calendarPagerAdapter.months[firstIndex]?.let {
                            for (day in it) {
                                if (day.type == MonthType.CURRENT) {
                                    onMonthSelect("${day.year}年${day.month}月")
                                    break
                                }
                            }
                        }
                    }
                })
            }
        }
    }
}