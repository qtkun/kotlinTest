package com.qtk.kotlintest.domain.data.room

import androidx.room.*

@Dao
interface ForecastDao {
    @Query("select * from city_forecast where id = :id")
    suspend fun getCityForecast(id : Long) : CityForecastRoom?

    @Query("select * from day_forecast where id = :id")
    suspend fun getDayForecast(id : Long) : DayForecastRoom

    @Query("select * from day_forecast where cityId = :id and date >= :date")
    suspend fun getDayForecasts(id : Long, date : Long) : List<DayForecastRoom>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityForecast(cityForecasts: CityForecastRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayForecast(dayForecastR: List<DayForecastRoom>)

    @Query("delete from city_forecast")
    suspend fun deleteCityForecast()

    @Query("delete from day_forecast")
    suspend fun deleteDayForecast()

    @Update
    suspend fun updateCityForecast(vararg city : CityForecastRoom)

    @Transaction
    suspend fun deleteAllAndInsertCityForecast(city: CityForecastRoom) {
        deleteCityForecast()
        insertCityForecast(city)
        deleteDayForecast()
        insertDayForecast(city.dailyForecast)
    }
}