package com.qtk.kotlintest.domain.command

import com.qtk.kotlintest.domain.datasource.ForecastProvider
import com.qtk.kotlintest.domain.model.ForecastList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Created by qtkun
on 2020-06-16.
 */
class RequestForecastCommand(private val zipCode: Long,
                             private val forecastProvider: ForecastProvider = ForecastProvider()) :
    Command<ForecastList> {
    companion object {
        const val DAYS = 7
    }

    override suspend fun execute(): ForecastList = withContext(Dispatchers.IO) {
        forecastProvider.requestByZipCode(zipCode, DAYS)
    }

    @ExperimentalCoroutinesApi
    override suspend fun execute2(): Flow<ForecastList> = flow {
        emit(forecastProvider.requestByZipCode(zipCode, DAYS))
    }.flowOn(Dispatchers.IO)

}