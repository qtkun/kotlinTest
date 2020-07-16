package com.qtk.kotlintest

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qtk.kotlintest.activities.BaseAdapter
import com.qtk.kotlintest.activities.BaseViewHolder
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.extensions.ctx
import com.qtk.kotlintest.extensions.textColor
import com.qtk.kotlintest.extensions.toDateString
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_forecast.*

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastListAdapter(dailyForecast:List<Forecast>?, itemClick : (Forecast) -> Unit) :
    BaseAdapter<ForecastListAdapter.ViewHolder, Forecast>(dailyForecast, itemClick, R.layout.item_forecast) {

    override fun initViewHolder(view: View, itemClick: (Forecast) -> Unit): ViewHolder = ViewHolder(view, itemClick)

    val forecast get() = items

    @SuppressLint("SetTextI18n")
    class ViewHolder(containerView: View, itemClick: (Forecast) -> Unit)
        : BaseViewHolder<Forecast>(containerView, itemClick) {

        override fun bind(t: Forecast) {
            with(t){
                Picasso.get().load(iconUrl).into(icon)
                dateText.text = date.toDateString()
                descriptionText.text = description
                descriptionText.textColor = Color.RED
                descriptionText.textSize = 16f
                maxTemperature.text = "${high}º"
                minTemperature.text = "${low}º"
            }
        }

    }
}