package com.qtk.kotlintest.domain.model


/**
 * Created by qtkun
on 2020-06-16.
 */
data class ForecastList(val id: Long, val city: String, val country: String,
                        val dailyForecast:List<Forecast>) {
    operator fun get(position: Int): Forecast = dailyForecast[position]
    val size : Int get() = dailyForecast.size
}

data class Forecast(val id: Long, val date: Long, val description: String,
                    val high: Int, val low: Int, val iconUrl: String)