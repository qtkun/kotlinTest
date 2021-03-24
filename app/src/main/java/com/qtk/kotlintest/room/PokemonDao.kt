package com.qtk.kotlintest.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qtk.kotlintest.room.entity.Location

@Dao
interface PokemonDao {
    @Query("select * from location")
    suspend fun getAllLocations() : List<Location>?

    @Query("select * from location where time >= :startTime and time <= :endTime order by time asc")
    suspend fun getLocations(startTime: Long, endTime: Long) : List<Location>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)
}