package com.qtk.kotlintest.domain.data.server

import com.qtk.kotlintest.domain.data.room.ForecastDbRoom
import com.qtk.kotlintest.domain.datasource.ForecastByZipCodeRequest
import com.qtk.kotlintest.domain.datasource.ForecastDataSource
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList

class ForecastServer(
    private val dataMapper: ServerDataMapper = ServerDataMapper(),
    private val forecastDb: ForecastDbRoom = ForecastDbRoom()
) : ForecastDataSource {
    override suspend fun requestForecastByZipCode(zipCode: Long, date: Long): ForecastList? {
        val result = ForecastByZipCodeRequest(zipCode).execute()
        val converted = dataMapper.convertToDomain(zipCode, result)
        forecastDb.saveForecast(converted)
        return forecastDb.requestForecastByZipCode(zipCode, date)
    }

    override suspend fun requestDayForecast(id: Long): Forecast? = throw UnsupportedOperationException()

}