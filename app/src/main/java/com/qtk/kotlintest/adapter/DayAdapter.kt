package com.qtk.kotlintest.adapter

import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemDayBinding
import com.qtk.kotlintest.utils.CalendarBean

class DayAdapter(days:List<CalendarBean>?, itemClick : (CalendarBean, ItemDayBinding) -> Unit) :
    BaseAdapter<CalendarBean, ItemDayBinding>(days, itemClick, R.layout.item_day) {}