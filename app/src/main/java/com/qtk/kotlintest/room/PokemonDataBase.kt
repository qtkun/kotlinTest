package com.qtk.kotlintest.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qtk.kotlintest.domain.data.room.Converter
import com.qtk.kotlintest.room.entity.Location

@Database(version = 1, entities = [Location::class])
@TypeConverters(Converter:: class)
abstract class PokemonDataBase: RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
}