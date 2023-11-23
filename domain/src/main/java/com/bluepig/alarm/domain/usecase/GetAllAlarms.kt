package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllAlarms @Inject constructor(
    private val _repository: AlarmRepository,
) {
    operator fun invoke() = _repository.getAllAlarmFlow()
        .map { it.sortedByDescending { it.timeInMillis } }
}