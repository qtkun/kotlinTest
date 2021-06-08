package com.qtk.kotlintest.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.base.update
import com.qtk.kotlintest.databinding.CalendarPageBinding
import com.qtk.kotlintest.utils.CalendarBean
import com.qtk.kotlintest.utils.getMonthDate
import com.qtk.kotlintest.utils.monthCount
import com.qtk.kotlintest.utils.positionToDate
import com.qtk.kotlintest.widget.smoothScroll

class CalendarPagerAdapter(
    private val recyclerView: RecyclerView,
): RecyclerView.Adapter<CalendarPagerAdapter.ViewHolder>() {
    private val _adapters = hashMapOf<Int, DayAdapter>()
    private val _months = hashMapOf<Int, List<CalendarBean>>()
    val months get() = _months

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (_months[position] == null) _months[position] = getMonthDate(positionToDate(position))
        holder.bindView(position)
    }

    override fun getItemCount(): Int = monthCount()

    inner class ViewHolder(val binding: CalendarPageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {
            with(binding.monthRv) {
                if (_adapters[position] == null) {
                    _months[position]?.let { days ->
                        val dayAdapter = DayAdapter(days) {item, _ ->
                            _months.forEach { (key, value) ->
                                when(item.type) {
                                    0 -> {
                                        notCurrent(key, value, item, position, -1)
                                    }
                                    1 -> {
                                        if (key == position) {
                                            value.forEach { day ->
                                                day.select = TextUtils.equals(day.dateString, item.dateString)
                                            }
                                        } else {
                                            value.forEach { day ->
                                                day.select = false
                                            }
                                        }
                                    }
                                    2 -> {
                                        notCurrent(key, value, item, position, 1)
                                    }
                                }
                                _adapters[key]?.update(value)
                            }
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
            if (exit.not()) {
                _months[position + offset] = getMonthDate(positionToDate(position + offset)).apply {
                    forEach { day ->
                        day.select = TextUtils.equals(day.dateString, item.dateString)
                    }
                    (recyclerView.layoutManager as LinearLayoutManager).smoothScroll(binding.root.context, position + offset)
                }
            }
        }
    }
}