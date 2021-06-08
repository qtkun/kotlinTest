package com.qtk.kotlintest.adapter

import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseListAdapter
import com.qtk.kotlintest.base.DiffUtilHelper
import com.qtk.kotlintest.databinding.ItemForecastBinding
import com.qtk.kotlintest.domain.model.Forecast

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastListAdapter2(dailyForecast:List<Forecast>?, itemClick : (Forecast, ItemForecastBinding) -> Unit) :
    BaseListAdapter<Forecast, ItemForecastBinding>(
        dailyForecast,
        itemClick,
        R.layout.item_forecast,
        object : DiffUtilHelper<Forecast>() {
            override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean = oldItem.id == newItem.id
        }
    )