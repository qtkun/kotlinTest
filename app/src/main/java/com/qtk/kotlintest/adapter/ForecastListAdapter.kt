package com.qtk.kotlintest.adapter

import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemForecastBinding
import com.qtk.kotlintest.domain.model.Forecast

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastListAdapter(dailyForecast:List<Forecast>?, itemClick : (Forecast, ViewDataBinding) -> Unit) :
    BaseAdapter<Forecast, ItemForecastBinding>(dailyForecast, itemClick, R.layout.item_forecast)