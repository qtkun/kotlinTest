package com.qtk.kotlintest.calender.adapter

import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.calender.CalendarBean
import com.qtk.kotlintest.calender.MonthType
import com.qtk.kotlintest.calender.adapter.DayAdapter
import com.qtk.kotlintest.calender.earlierThanToday
import com.qtk.kotlintest.calender.getMonthDate
import com.qtk.kotlintest.calender.monthCount
import com.qtk.kotlintest.calender.positionToDate
import com.qtk.kotlintest.databinding.CalendarPageBinding
import com.qtk.kotlintest.widget.smoothScroll
import org.jetbrains.anko.toast

class CalendarAdapter(
        private val initialDate: IntArray,
        private val recyclerView: RecyclerView,
        private val canSelectBeforeToday: Boolean,
        private val onSelect: (CalendarBean) -> Unit
): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private val _adapters = SparseArray<DayAdapter>()
    private val _months = SparseArray<List<CalendarBean>>()

    val months get() = _months

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (_months[position] == null) _months[position] = getMonthDate(positionToDate(position), initialDate)
        holder.bindView(position)
    }

    override fun getItemCount(): Int = monthCount()

    inner class ViewHolder(val binding: CalendarPageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {
            with(binding.monthRv) {
                if (_adapters[position] == null) {
                    _months[position]?.let { days ->
                        val dayAdapter = DayAdapter(days) { item ->
                            if (!canSelectBeforeToday) {
                                if (item.dateString.earlierThanToday()) {
                                    binding.root.post {
                                        context.toast("只能选择今天及以后")
                                    }
                                    return@DayAdapter
                                }
                            }
                            _months.forEach { key, value ->
                                when(item.type) {
                                    MonthType.PREVIOUS -> {
                                        notCurrent(key, value, item, position, -1)
                                    }
                                    MonthType.CURRENT -> {
                                        if (key == position) {
                                            value.forEach { day ->
                                                day.select = day.dateString == item.dateString
                                            }
                                        } else {
                                            for (calendarBean in value) {

                                            }
                                            value.forEach { day ->
                                                day.select = false
                                            }
                                        }
                                    }
                                    MonthType.NEXT -> {
                                        notCurrent(key, value, item, position, 1)
                                    }
                                }
                                _adapters[key]?.update(value)
                            }
                            onSelect(item)
                        }
                        _adapters[position] = dayAdapter
                    }
                }
                adapter = _adapters[position]
                layoutManager = GridLayoutManager(binding.root.context, 7)
            }
        }

        private fun notCurrent(key: Int, value: List<CalendarBean>, item: CalendarBean, position: Int, offset: Int) {
            var exit = false
            if (key == position + offset) {
                exit = true
                value.forEach { day ->
                    day.select = TextUtils.equals(day.dateString, item.dateString)
                }
                (recyclerView.layoutManager as LinearLayoutManager).smoothScroll(binding.root.context, key)
            } else {
                value.forEach { day ->
                    day.select = false
                }
            }
            if (!exit) {
                _months[position + offset] = getMonthDate(positionToDate(position + offset), initialDate).apply {
                    forEach { day ->
                        day.select = TextUtils.equals(day.dateString, item.dateString)
                    }
                    (recyclerView.layoutManager as LinearLayoutManager).smoothScroll(binding.root.context, position + offset)
                }
            }
        }
    }
}