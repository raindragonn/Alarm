package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.data.database.data.AlarmData
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week.FRIDAY
import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import org.junit.Assert.assertEquals
import org.junit.Test

// 값을 Alarm 기본값과 다르게 둬서 빠진 필드를 잡는다
class AlarmMapperTest {
    private val media = RingtoneMedia(12, "Beep", "content://media/internal/audio/media/12")

    private val data = AlarmData(
        id = 7,
        timeInMillis = 1_793_000_000_000,
        isActive = false,
        alarmMedia = media,
        repeatWeek = setOf(MONDAY, FRIDAY),
        volume = 9,
        isVolumeAutoIncrease = false,
        hasVibration = false,
        memo = "물 마시기",
        memoTtsEnabled = true,
    )

    private val alarm = Alarm(
        id = 7,
        timeInMillis = 1_793_000_000_000,
        isActive = false,
        media = media,
        repeatWeek = setOf(MONDAY, FRIDAY),
        volume = 9,
        isVolumeAutoIncrease = false,
        hasVibration = false,
        memo = "물 마시기",
        memoTtsEnabled = true,
    )

    @Test
    fun `AlarmData를 Alarm으로`() = assertEquals(alarm, AlarmMapper.mapToEntity(data))

    @Test
    fun `Alarm을 AlarmData로`() = assertEquals(data, AlarmMapper.mapToData(alarm))
}
