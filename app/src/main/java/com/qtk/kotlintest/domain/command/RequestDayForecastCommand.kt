package com.qtk.kotlintest.domain.command

import com.qtk.kotlintest.domain.datasource.ForecastProvider
import com.qtk.kotlintest.domain.model.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RequestDayForecastCommand(
    val id: Long,
    private val forecastProvider: ForecastProvider = ForecastProvider()
) : Command<Forecast> {
    override suspend fun execute(): Forecast = withContext(Dispatchers.IO) {forecastProvider.requestForecast(id)}

    @ExperimentalCoroutinesApi
    override suspend fun execute2(): Flow<Forecast> = flow {
        emit(forecastProvider.requestForecast(id))
    }.flowOn(Dispatchers.IO)

}