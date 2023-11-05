package com.bluepig.mapper

import com.bluepig.alarm.database.data.AlarmData
import com.bluepig.alarm.domain.entity.Alarm

object AlarmMapper : BaseMapper<AlarmData, Alarm> {
    override fun mapToEntity(data: AlarmData): Alarm =
        Alarm(
            id = data.id,
            timeInMillis = data.timeInMillis,
            isActive = data.isActive,
            songName = data.songName,
        )

    override fun mapToData(entity: Alarm): AlarmData =
        AlarmData(
            id = entity.id,
            timeInMillis = entity.timeInMillis,
            isActive = entity.isActive,
            songName = entity.songName,
        )
}