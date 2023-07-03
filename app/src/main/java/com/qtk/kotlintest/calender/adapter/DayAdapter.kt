package com.qtk.kotlintest.calender.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.calender.CalendarBean
import com.qtk.kotlintest.calender.MonthType
import com.qtk.kotlintest.calender.isToday
import com.qtk.kotlintest.databinding.ItemDayBinding
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.textColor

class DayAdapter(
    private var days:List<CalendarBean>?,
    private val itemClick : (CalendarBean) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = days?.size ?: 0

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        days?.get(position)?.let {
            holder.bind(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(days:List<CalendarBean>) {
        this.days = days
        notifyDataSetChanged()
    }

    inner class DayViewHolder(private val binding: ItemDayBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CalendarBean) {
            with(binding) {
                root.setOnClickListener {
                    itemClick.invoke(item)
                }
                tvDay.text = item.day.toString()
                when (item.type) {
                    MonthType.PREVIOUS, MonthType.NEXT -> {
                        selectBg.isVisible = false
                        tvDay.textColor = root.context.color(R.color.text_secondary)
                    }
                    MonthType.CURRENT -> {
                        selectBg.isVisible = item.select
                        tvDay.textColor = if (item.select) {
                            root.context.color(R.color.white)
                        } else {
                            if (item.dateString.isToday()) {
                                tvDay.setBackgroundColor(Color.parseColor("#EAEEFF"))
                            } else {
                                tvDay.setBackgroundColor(root.context.color(R.color.white))
                            }
                            if (item.day == 1) {
                                tvDay.text = "${item.month}月"
                                root.context.color(R.color.main)
                            } else {
                                root.context.color(R.color.text_main)
                            }
                        }
                    }
                }
            }
        }
    }
}