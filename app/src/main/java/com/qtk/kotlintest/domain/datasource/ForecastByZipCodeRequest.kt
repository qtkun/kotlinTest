package com.qtk.kotlintest.domain.datasource

import android.util.Log
import com.google.gson.Gson
import com.qtk.kotlintest.domain.data.server.ForecastResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastByZipCodeRequest(private val zipCode: Long, val gson: Gson = Gson()) {
    companion object {
        private const val APP_ID = "15646a06818f61f7b8d7823ca833e1ce"
        private const val URL = "http://api.openweathermap.org/data/2.5/" +
                "forecast/daily?mode=json&units=metric&cnt=7"
        private const val COMPLETE_URL = "$URL&APPID=$APP_ID&q="
    }

    suspend fun execute(): ForecastResult {
        return withContext(Dispatchers.IO){
            val url = COMPLETE_URL + zipCode
            val forecastJsonStr = URL(url).readText().ifEmpty { "{}" }
            Log.i("qtk", forecastJsonStr)
            gson.fromJson(forecastJsonStr, ForecastResult::class.java)
        }
    }
}