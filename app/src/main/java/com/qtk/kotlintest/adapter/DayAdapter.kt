package com.qtk.kotlintest.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemDayBinding
import com.qtk.kotlintest.extensions.color
import com.qtk.kotlintest.extensions.textColor
import com.qtk.kotlintest.utils.CalendarBean

class DayAdapter(days:List<CalendarBean>?, itemClick : (CalendarBean, ItemDayBinding) -> Unit) :
    BaseAdapter<CalendarBean, ItemDayBinding>(days, itemClick, R.layout.item_day) {
    override fun onBind(binding: ItemDayBinding, item: CalendarBean) {
        with(binding) {
            when (item.type) {
                0, 2 -> {
                    selectBg.visibility = View.GONE
                    day.textColor = root.context.color(R.color.text_secondary)
                }
                1 -> {
                    day.textColor = if (item.select) {
                        selectBg.visibility = View.VISIBLE
                        root.context.color(R.color.white)
                    } else {
                        selectBg.visibility = View.GONE
                        if (item.day == 1) {
                            day.text = "${item.month}月"
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