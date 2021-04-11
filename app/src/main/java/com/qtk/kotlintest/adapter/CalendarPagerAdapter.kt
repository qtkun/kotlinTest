package com.qtk.kotlintest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.CalendarPageBinding
import com.qtk.kotlintest.utils.CalendarBean
import com.qtk.kotlintest.utils.getMonthDate
import com.qtk.kotlintest.utils.monthCount
import com.qtk.kotlintest.utils.positionToDate

class CalendarPagerAdapter: RecyclerView.Adapter<CalendarPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getMonthDate(positionToDate(position)))
    }

    override fun getItemCount(): Int = monthCount()

    class ViewHolder(val binding: CalendarPageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(days: List<CalendarBean>) {
            with(binding.monthRv) {
                adapter = DayAdapter(days) {_,_ ->}
                layoutManager = GridLayoutManager(binding.root.context, 7)
            }
        }
    }
}