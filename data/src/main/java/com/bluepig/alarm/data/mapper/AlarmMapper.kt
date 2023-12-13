package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.data.database.data.AlarmData
import com.bluepig.alarm.domain.entity.alarm.Alarm

object AlarmMapper : BaseMapper<AlarmData, Alarm> {
    override fun mapToEntity(data: AlarmData): Alarm =
        Alarm(
            id = data.id,
            timeInMillis = data.timeInMillis,
            isActive = data.isActive,
            media = data.alarmMedia,
            repeatWeek = data.repeatWeek,
            volume = data.volume,
            hasVibration = data.hasVibration,
            memo = data.memo,
        )

    override fun mapToData(entity: Alarm): AlarmData =
        AlarmData(
            id = entity.id,
            timeInMillis = entity.timeInMillis,
            isActive = entity.isActive,
            repeatWeek = entity.repeatWeek,
            volume = entity.volume,
            hasVibration = entity.hasVibration,
            memo = entity.memo,
            alarmMedia = entity.media
        )
}