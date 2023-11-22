package com.bluepig.alarm.database.converter

import androidx.room.TypeConverter
import com.bluepig.alarm.domain.entity.file.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FileTypeConverter {
    @TypeConverter
    fun jsonStringToWeakSet(data: String): File {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun weakSetToJsonString(data: File): String {
        return Json.encodeToString(data)
    }
}