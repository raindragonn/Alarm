package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlarmById @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
) {
    suspend operator fun invoke(id: Long) =
        withContext(_dispatcher) {
            kotlin.runCatching {
                _repository.getById(id) ?: throw NotFoundAlarmException
            }
        }
}