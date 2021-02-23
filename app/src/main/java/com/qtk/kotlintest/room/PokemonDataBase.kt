package com.qtk.kotlintest.room

import androidx.room.RoomDatabase

abstract class PokemonDataBase: RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
}