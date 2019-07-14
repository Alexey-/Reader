package com.example.base.model.database.convertors

import androidx.room.TypeConverter
import java.io.File

class FileConvertor {

    @TypeConverter
    fun toFile(value: String): File? {
        return if (value.isNotBlank()) {
            null
        } else {
            File(value)
        }
    }

    @TypeConverter
    fun toPath(file: File?): String? {
        return file?.absolutePath
    }

}
