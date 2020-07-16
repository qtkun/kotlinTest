package com.qtk.kotlintest.domain.datasource

import com.qtk.kotlintest.domain.data.room.ForecastDbRoom
import com.qtk.kotlintest.domain.data.server.ForecastServer
import com.qtk.kotlintest.domain.model.Forecast
import com.qtk.kotlintest.domain.model.ForecastList
import com.qtk.kotlintest.extensions.firstResult

class ForecastProvider(private val sources: List<ForecastDataSource> = SOURCES) {
    companion object {
        const val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
        val SOURCES = listOf(ForecastDbRoom(), ForecastServer())
    }

    suspend fun requestByZipCode(zipCode: Long, days: Int): ForecastList
            = requestToSources {
        val res = it.requestForecastByZipCode(zipCode, todayTimeSpan())
        if (res != null && res.size >= days) res else null
    }

    private fun todayTimeSpan() = System.currentTimeMillis() / DAY_IN_MILLIS * DAY_IN_MILLIS

    private suspend fun <T : Any> requestToSources(f : suspend(ForecastDataSource) -> T?) : T = sources.firstResult { f(it) }

    suspend fun requestForecast(id: Long): Forecast = requestToSources {
        it.requestDayForecast(id)
    }
}