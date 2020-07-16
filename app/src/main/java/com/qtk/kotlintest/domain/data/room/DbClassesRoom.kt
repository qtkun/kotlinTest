package com.qtk.kotlintest.domain.data.room

import androidx.room.*

@Entity(tableName = "city_forecast")
data class CityForecastRoom(
    @PrimaryKey
    var id: Long,
    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "country")
    var country: String
){
    @Ignore
    var dailyForecast: List<DayForecastRoom> = emptyList()
}

@Entity(tableName = "day_forecast")
data class DayForecastRoom(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "date")
    var date: Long,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "high")
    var high: Int,
    @ColumnInfo(name = "low")
    var low: Int,
    @ColumnInfo(name = "iconUrl")
    var iconUrl: String,
    @ColumnInfo(name = "cityId")
    var cityId: Long
)