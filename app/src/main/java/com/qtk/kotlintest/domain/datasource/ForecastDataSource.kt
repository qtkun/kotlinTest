package com.qtk.kotlintest.domain.datasource

import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList

interface ForecastDataSource {
    suspend fun requestForecastByZipCode(zipCode: Long, date: Long): ForecastList?

    suspend fun requestDayForecast(id: Long): Forecast?
}