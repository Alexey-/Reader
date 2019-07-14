package com.example.base.model.database.convertors

import androidx.room.TypeConverter
import org.joda.time.LocalDateTime

class LocalDateTimeConvertor {

    @TypeConverter
    fun toLocalDateTime(value: Long): LocalDateTime? {
        return if (value == 0L) {
            null
        } else {
            LocalDateTime(value)
        }
    }

    @TypeConverter
    fun toInstant(localDateTime: LocalDateTime?): Long {
        return localDateTime?.toDate()?.time ?: 0
    }

}
