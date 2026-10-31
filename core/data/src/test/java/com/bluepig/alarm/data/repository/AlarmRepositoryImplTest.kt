package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.fake.FakeAlarmLocalDataSource
import com.bluepig.alarm.data.fake.FakePreferencesLocalDataSource
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlarmRepositoryImplTest {
    private val localDataSource = FakeAlarmLocalDataSource()
    private val repository = AlarmRepositoryImpl(localDataSource, FakePreferencesLocalDataSource())

    private val alarm = Alarm(
        timeInMillis = 1_793_000_000_000,
        media = TubeMedia("abcdefghijk", "https://i.ytimg.com/vi/abcdefghijk/default.jpg", "기상 음악"),
        repeatWeek = setOf(MONDAY),
        volume = 3,
    )

    @Test
    fun `저장한 알람을 그대로 읽는다`() = runTest {
        val id = repository.insertAlarm(alarm)

        val saved = alarm.copy(id = id)
        assertEquals(saved, repository.getById(id))
        assertEquals(listOf(saved), repository.getAllAlarms())
        assertEquals(listOf(saved), repository.getAllAlarmFlow().first())
    }

    @Test
    fun `지운 알람은 읽히지 않는다`() = runTest {
        val id = repository.insertAlarm(alarm)

        repository.deleteById(id)

        assertNull(repository.getById(id))
        assertEquals(emptyList<Alarm>(), repository.getAllAlarms())
    }
}
