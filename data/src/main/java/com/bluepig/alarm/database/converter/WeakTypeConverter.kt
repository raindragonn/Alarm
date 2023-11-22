package com.bluepig.alarm.database.converter

import androidx.room.TypeConverter
import com.bluepig.alarm.domain.entity.alarm.Weak
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeakTypeConverter {

    @TypeConverter
    fun jsonStringToWeakSet(data: String): Set<Weak> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun weakSetToJsonString(data: Set<Weak>): String {
        return Json.encodeToString(data)
    }
}