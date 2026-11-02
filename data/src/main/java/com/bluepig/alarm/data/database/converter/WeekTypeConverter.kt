package com.bluepig.alarm.data.database.converter

import androidx.room.TypeConverter
import com.bluepig.alarm.domain.entity.alarm.Week
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeekTypeConverter {

    @TypeConverter
    fun jsonStringToWeekSet(data: String): Set<Week> {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun weekSetToJsonString(data: Set<Week>): String {
        return Json.encodeToString(data)
    }
}