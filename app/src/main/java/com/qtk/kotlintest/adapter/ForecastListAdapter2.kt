package com.qtk.kotlintest.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.qtk.kotlintest.R
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
class ForecastListAdapter2(dailyForecast:List<Forecast>?, itemClick : (Forecast) -> Unit) :
    BaseListAdapter<ForecastListAdapter2.ViewHolder, Forecast>(
        dailyForecast,
        itemClick,
        R.layout.item_forecast,
        object : DiffUtilHelper<Forecast>() {
            override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean = oldItem.id == newItem.id
        }
    ) {
    override fun initViewHolder(view: View, itemClick: (Forecast) -> Unit): ViewHolder =
        ViewHolder(view, itemClick)

    @SuppressLint("SetTextI18n")
    class ViewHolder(containerView: View, itemClick: (Forecast) -> Unit)
        : BaseViewHolder<Forecast>(containerView, itemClick) {
        private val binding by bindItemView<ItemForecastBinding>(containerView)
        override fun bind(t: Forecast) {
            with(t){
                with(binding){
                    Picasso.get().load(iconUrl).into(binding.icon)
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
}