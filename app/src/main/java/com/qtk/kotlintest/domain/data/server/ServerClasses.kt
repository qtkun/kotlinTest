package com.qtk.kotlintest.domain.data.server

import com.squareup.moshi.JsonClass

/**
 * Created by qtkun
on 2020-06-15.
 */
@JsonClass(generateAdapter = true)
data class ForecastResult(val city: City, val list: List<Forecast>)
@JsonClass(generateAdapter = true)
data class City(val id: Long, val name: String, val coord: Coordinates,
                val country: String, val population: Int)
@JsonClass(generateAdapter = true)
data class Coordinates(val lon: Float, val lat: Float)
@JsonClass(generateAdapter = true)
data class Forecast(val dt: Long, val temp: Temperature, val pressure: Float,
                    val humidity: Int, val weather: List<Weather>,
                    val speed: Float, val deg: Int, val clouds: Int,
                    val rain: Float)
@JsonClass(generateAdapter = true)
data class Temperature(val day: Float, val min: Float, val max: Float,
                       val night: Float, val eve: Float, val morn: Float)
@JsonClass(generateAdapter = true)
data class Weather(val id: Long, val main: String, val description: String,
                   val icon: String)