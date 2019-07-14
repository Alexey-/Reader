package com.example.base.model.database.convertors

import androidx.room.TypeConverter
import org.joda.time.LocalDate

class LocalDateConvertor {

    @TypeConverter
    fun toLocalDateTime(value: Long): LocalDate? {
        return if (value == 0L) {
            null
        } else {
            LocalDate(value)
        }
    }

    @TypeConverter
    fun toInstant(localDate: LocalDate?): Long {
        return localDate?.toDate()?.time ?: 0
    }

}
