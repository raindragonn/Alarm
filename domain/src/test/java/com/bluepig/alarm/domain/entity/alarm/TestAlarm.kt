package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia

fun testAlarm(
    timeInMillis: Long,
    repeatWeek: Set<Week> = emptySet(),
    isActive: Boolean = true,
    id: Long? = null,
) = Alarm(
    id = id,
    timeInMillis = timeInMillis,
    isActive = isActive,
    media = RingtoneMedia(1, "기본 알람음", "content://media/internal/audio/media/1"),
    repeatWeek = repeatWeek,
    volume = 5,
)
