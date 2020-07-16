package com.qtk.kotlintest.domain.data.room

import androidx.room.TypeConverter
import java.util.*

class Converter {
    @TypeConverter
    fun longToDate(timeStamp: Long?): Date? {
        if (timeStamp == null) return null
        return Date(timeStamp)
    }

    @TypeConverter
    fun dateToLong(date: Date?): Long? {
        if (date == null) return null
        return date.time
    }
}