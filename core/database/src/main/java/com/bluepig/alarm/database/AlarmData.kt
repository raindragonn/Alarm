package com.bluepig.alarm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** [alarmMedia]·[repeatWeek]는 core:data가 domain 모델과 변환하는 JSON */
@Entity(tableName = "alarm")
data class AlarmData(
    @PrimaryKey val id: Long? = null,
    @ColumnInfo("timeInMillis") val timeInMillis: Long,
    @ColumnInfo("isActive") val isActive: Boolean,
    @ColumnInfo("alarmMedia") val alarmMedia: String,
    @ColumnInfo("repeatWeek") val repeatWeek: String,
    @ColumnInfo("volume") val volume: Int,
    @ColumnInfo("isVolumeAutoIncrease") val isVolumeAutoIncrease: Boolean = true,
    @ColumnInfo("hasVibration") val hasVibration: Boolean,
    @ColumnInfo("memo") val memo: String,
    @ColumnInfo("memoTtsEnabled") val memoTtsEnabled: Boolean,
)
