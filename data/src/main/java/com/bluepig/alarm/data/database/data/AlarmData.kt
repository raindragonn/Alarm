package com.bluepig.alarm.data.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.file.SongFile

@Entity(tableName = "alarm")
data class AlarmData(
    @PrimaryKey val id: Long? = null,
    @ColumnInfo("timeInMillis") val timeInMillis: Long,
    @ColumnInfo("isActive") val isActive: Boolean,
    @ColumnInfo("file") val file: SongFile,
    @ColumnInfo("repeatWeek") val repeatWeek: Set<Week>,
    @ColumnInfo("volume") val volume: Int? = null,
    @ColumnInfo("hasVibration") val hasVibration: Boolean,
    @ColumnInfo("memo") val memo: String,
) : BaseData