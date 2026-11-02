package com.bluepig.alarm.data.database.converter

import androidx.room.TypeConverter
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AlarmMediaTypeConverter {
    @TypeConverter
    fun jsonStringToAlarmMedia(data: String): AlarmMedia {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun alarmMediaToJsonString(data: AlarmMedia): String {
        return Json.encodeToString(data)
    }
}