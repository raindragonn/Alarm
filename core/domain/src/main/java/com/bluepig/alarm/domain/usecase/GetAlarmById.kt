package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.runSuspendCatching
import javax.inject.Inject

class GetAlarmById @Inject constructor(
    private val _repository: AlarmRepository,
) {
    suspend operator fun invoke(id: Long) = runSuspendCatching {
        _repository.getById(id) ?: throw NotFoundAlarmException
    }
}
