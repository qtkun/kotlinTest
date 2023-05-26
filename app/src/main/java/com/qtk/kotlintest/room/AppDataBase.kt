package com.qtk.kotlintest.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qtk.kotlintest.domain.data.room.CityForecastRoom
import com.qtk.kotlintest.domain.data.room.Converter
import com.qtk.kotlintest.domain.data.room.DayForecastRoom
import com.qtk.kotlintest.room.entity.ChatMessageBean
import com.qtk.kotlintest.room.entity.Location

@Database(version = 1, entities = [
    Location::class,
    CityForecastRoom::class,
    DayForecastRoom:: class,
    ChatMessageBean::class
])
@TypeConverters(Converter:: class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
    abstract fun getForecastDao(): ForecastDao
    abstract fun getChatGPTDao(): ChatGPTDao
}