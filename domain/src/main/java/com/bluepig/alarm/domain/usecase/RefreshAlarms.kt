package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshAlarms @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
    private val _saveAlarm: SaveAlarm,
) {
    suspend operator fun invoke() = withContext(_dispatcher) {
        _repository.getAllAlarms()
            .filter(Alarm::isActive)
            .map(Alarm::getActiveCheckedAlarm)
            .forEach {
                _saveAlarm.invoke(it)
            }
    }
}