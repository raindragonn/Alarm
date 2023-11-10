package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import javax.inject.Inject

class GetAllAlarms @Inject constructor(
    private val _repository: AlarmRepository,
) {
    operator fun invoke() = _repository.getAllAlarmFlow()
}