package com.qtk.kotlintest.domain.command

import com.qtk.kotlintest.domain.datasource.ForecastProvider
import com.qtk.kotlintest.domain.model.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RequestDayForecastCommand(
    val id: Long,
    private val forecastProvider: ForecastProvider = ForecastProvider()
) : Command<Forecast> {
    override suspend fun execute(): Forecast = withContext(Dispatchers.IO) {forecastProvider.requestForecast(id)}

}