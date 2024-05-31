package com.example.paisefy_moneymanager.utils

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimeStampToDate(value: Long): Date? {
        return value?.let {
            Date(it)
        }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}