package com.qtk.kotlintest.domain.data.room

import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList

class DbDataMapperRoom {
    fun convertFromDomain(forecast: ForecastList) = with(forecast) {
        val daily = dailyForecast.map {
            convertDayFromDomain(id, it)
        }
        CityForecastRoom(id, city, country)
            .apply { this.dailyForecast = daily }
    }

    private fun convertDayFromDomain(cityId: Long, forecast: Forecast) = with(forecast) {
        DayForecastRoom(
            null,
            date,
            description,
            high,
            low,
            iconUrl,
            cityId
        )
    }

    fun convertToDomain(forecast: CityForecastRoom) = with(forecast) {
        val daily = dailyForecast?.map { convertDayToDomain(it) }
        ForecastList(id, city, country, daily as ArrayList<Forecast>)
    }

    fun convertDayToDomain(dayForecast: DayForecastRoom) = with(dayForecast) {
        Forecast(id!!, date, description, high, low, iconUrl)
    }
}