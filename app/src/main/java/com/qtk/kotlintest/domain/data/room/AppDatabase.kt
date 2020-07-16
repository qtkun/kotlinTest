package com.qtk.kotlintest.domain.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [CityForecastRoom:: class, DayForecastRoom:: class])
@TypeConverters(Converter:: class)
abstract class AppDatabase : RoomDatabase() {
    companion object{
        private const val DB_NAME = "forecast.db"
        private var INSTANCE: AppDatabase? = null
        private var lock = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    // 第一个参数是Context, 第二个参数是DB的class，第三个参数是DB名字
                    INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java,
                        DB_NAME
                    ).build()
                }

                return INSTANCE!!
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    abstract fun getForecastDao(): ForecastDao
}