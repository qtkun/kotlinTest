package com.qtk.kotlintest.domain.datasource

import android.util.Log
import com.google.gson.Gson
import com.qtk.kotlintest.contant.COMPLETE_URL
import com.qtk.kotlintest.domain.data.server.ForecastResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Created by qtkun
on 2020-06-15.
 */
class ForecastByZipCodeRequest(private val zipCode: Long, val gson: Gson = Gson()) {
    suspend fun execute(): ForecastResult {
        return withContext(Dispatchers.IO){
            val url = COMPLETE_URL + zipCode
            val forecastJsonStr = URL(url).readText().ifEmpty { "{}" }
            Log.i("qtk", forecastJsonStr)
            gson.fromJson(forecastJsonStr, ForecastResult::class.java)
        }
    }
}