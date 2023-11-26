package com.bluepig.alarm.data.database.converter

import androidx.room.TypeConverter
import com.bluepig.alarm.domain.entity.file.SongFile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SongFileTypeConverter {
    @TypeConverter
    fun jsonStringToSongFile(data: String): SongFile {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun songFileToJsonString(data: SongFile): String {
        return Json.encodeToString(data)
    }
}