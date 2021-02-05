package com.qtk.kotlintest.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemForecastBinding
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.extensions.bindItemView
import com.qtk.kotlintest.extensions.textColor
import com.qtk.kotlintest.extensions.toDateString
import com.squareup.picasso.Picasso

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastListAdapter(dailyForecast:List<Forecast>?, itemClick : (Forecast) -> Unit) :
    BaseAdapter<Forecast>(dailyForecast, itemClick, R.layout.item_forecast) {
    override fun onBind(binding: ViewDataBinding, item: Forecast) {
        TODO("Not yet implemented")
    }

}