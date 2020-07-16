package com.qtk.kotlintest.domain.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.qtk.kotlintest.App
import org.jetbrains.anko.db.*

/**
 * Created by qtkun
on 2020-06-16.
 */
class ForecastDbHelper(ctx : Context = App.instance) : ManagedSQLiteOpenHelper(ctx,
    ForecastDbHelper.DB_NAME, null, ForecastDbHelper.DB_VERSION) {
    companion object {
        const val DB_NAME = "forecast.db"
        const val DB_VERSION = 1
        val instance: ForecastDbHelper by lazy { ForecastDbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(CityForecastTable.NAME,true,
            CityForecastTable.ID to  INTEGER + PRIMARY_KEY,
            CityForecastTable.COUNTRY to  TEXT,
            CityForecastTable.CITY to TEXT
        )

        db?.createTable(DayForecastTable.NAME, true,
            DayForecastTable.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            DayForecastTable.DATE to INTEGER,
            DayForecastTable.DESCRIPTION to TEXT,
            DayForecastTable.HIGH to INTEGER,
            DayForecastTable.LOW to INTEGER,
            DayForecastTable.ICON_URL to TEXT,
            DayForecastTable.CITY_ID to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.dropTable(CityForecastTable.NAME, true)
        db?.dropTable(DayForecastTable.NAME, true)
        onCreate(db)
    }
}