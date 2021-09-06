package com.qtk.kotlintest.adapter

import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.base.DiffCallBack
import com.qtk.kotlintest.databinding.ItemForecastBinding
import com.qtk.kotlintest.domain.model.Forecast

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastListAdapter(dailyForecast:List<Forecast>?, itemClick : (Forecast, ViewDataBinding) -> Unit) :
    BaseAdapter<Forecast, ItemForecastBinding>(dailyForecast, itemClick, R.layout.item_forecast)

class ForecastDiffCallBack(oldItems: List<Forecast>, newItems: List<Forecast>): DiffCallBack<Forecast>(oldItems, newItems) {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].date == newItems[newItemPosition].date
    }
}