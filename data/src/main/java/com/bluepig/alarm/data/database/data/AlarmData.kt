package com.bluepig.alarm.data.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia

@Entity(tableName = "alarm")
data class AlarmData(
    @PrimaryKey val id: Long? = null,
    @ColumnInfo("timeInMillis") val timeInMillis: Long,
    @ColumnInfo("isActive") val isActive: Boolean,
    @ColumnInfo("alarmMedia") val alarmMedia: AlarmMedia,
    @ColumnInfo("repeatWeek") val repeatWeek: Set<Week>,
    @ColumnInfo("volume") val volume: Int,
    @ColumnInfo("isVolumeAutoIncrease") val isVolumeAutoIncrease: Boolean = true,
    @ColumnInfo("hasVibration") val hasVibration: Boolean,
    @ColumnInfo("memo") val memo: String,
    @ColumnInfo("memoTtsEnabled") val memoTtsEnabled: Boolean,
) : BaseData
