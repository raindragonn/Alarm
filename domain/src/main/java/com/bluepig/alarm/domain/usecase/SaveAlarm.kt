package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveAlarm @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val repository: AlarmRepository,
) {
    suspend operator fun invoke(alarm: Alarm) =
        asyncResultWithContextOf(_dispatcher) {
            val id = repository.insertAlarm(alarm)
            repository.getById(id) ?: throw NullPointerException("Not Found Saved Alarm..id:$id")
        }
}