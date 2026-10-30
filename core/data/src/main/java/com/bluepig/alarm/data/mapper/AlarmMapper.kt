package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.database.AlarmData
import com.bluepig.alarm.domain.entity.alarm.Alarm

internal fun AlarmData.toDomain() = Alarm(
    id = id,
    timeInMillis = timeInMillis,
    isActive = isActive,
    media = alarmMediaFromJson(alarmMedia),
    repeatWeek = weeksFromJson(repeatWeek),
    volume = volume,
    isVolumeAutoIncrease = isVolumeAutoIncrease,
    hasVibration = hasVibration,
    memo = memo,
    memoTtsEnabled = memoTtsEnabled,
)

internal fun Alarm.toData() = AlarmData(
    id = id,
    timeInMillis = timeInMillis,
    isActive = isActive,
    alarmMedia = media.toJson(),
    repeatWeek = repeatWeek.toJson(),
    volume = volume,
    isVolumeAutoIncrease = isVolumeAutoIncrease,
    hasVibration = hasVibration,
    memo = memo,
    memoTtsEnabled = memoTtsEnabled,
)
