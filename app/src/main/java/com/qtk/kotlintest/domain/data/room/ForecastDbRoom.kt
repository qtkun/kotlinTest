package com.qtk.kotlintest.domain.data.room

import com.qtk.kotlintest.App
import com.qtk.kotlintest.domain.datasource.ForecastDataSource
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastDbRoom(
    private val dataMapper: DbDataMapperRoom = DbDataMapperRoom()
) : ForecastDataSource {
    override suspend fun requestForecastByZipCode(zipCode: Long, date: Long): ForecastList? {
        return withContext(Dispatchers.IO) {
            AppDatabase.getInstance(App.instance.applicationContext).getForecastDao()
                .getCityForecast(zipCode)?.let { cityForecastR ->
                    dataMapper.convertToDomain(cityForecastR.apply {
                        AppDatabase.getInstance(App.instance.applicationContext).getForecastDao()
                            .getDayForecasts(zipCode, date)?.let { dailyForecast = it }
                    })
                }
        }
    }

    override suspend fun requestDayForecast(id: Long): Forecast? {
        return withContext(Dispatchers.IO) {
            AppDatabase.getInstance(App.instance.applicationContext).getForecastDao()
                .getDayForecast(id).let { dataMapper.convertDayToDomain(it) }
        }
    }

    suspend fun saveForecast(forecast: ForecastList) {
        withContext(Dispatchers.IO) {
            AppDatabase.getInstance(App.instance.applicationContext).getForecastDao()
                .deleteAllAndInsertCityForecast(dataMapper.convertFromDomain(forecast))
        }
    }

}