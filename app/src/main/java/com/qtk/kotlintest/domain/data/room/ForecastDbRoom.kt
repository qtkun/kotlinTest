package com.qtk.kotlintest.domain.data.room

import com.qtk.kotlintest.App
import com.qtk.kotlintest.domain.datasource.ForecastDataSource
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.modules.ForecastDaoEntryPoint
import com.qtk.kotlintest.room.ForecastDao
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForecastDbRoom(
    private val dataMapper: DbDataMapperRoom = DbDataMapperRoom()
) : ForecastDataSource {
    private val forecastDao: ForecastDao = EntryPointAccessors.fromApplication<ForecastDaoEntryPoint>(App.instance).getForecastDao()

    override suspend fun requestForecastByZipCode(zipCode: Long, date: Long): ForecastList? {
        return withContext(Dispatchers.IO) {
            forecastDao.getCityForecast(zipCode)?.let { cityForecastR ->
                dataMapper.convertToDomain(cityForecastR.apply {
                    forecastDao.getDayForecasts(zipCode, date)?.let { dailyForecast = it }
                })
            }
        }
    }

    override suspend fun requestDayForecast(id: Long): Forecast {
        return withContext(Dispatchers.IO) {
            forecastDao.getDayForecast(id).let { dataMapper.convertDayToDomain(it) }
        }
    }

    suspend fun saveForecast(forecast: ForecastList) {
        withContext(Dispatchers.IO) {
            forecastDao.deleteAllAndInsertCityForecast(dataMapper.convertFromDomain(forecast))
        }
    }

}