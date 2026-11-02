package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.RingtoneRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRingtones @Inject constructor(
    @IoDispatcher private val _dispatcher: CoroutineDispatcher,
    private val _repository: RingtoneRepository,
) {
    suspend operator fun invoke() = withContext(_dispatcher) {
        kotlin.runCatching {
            _repository.getRingtoneList()
        }
    }
}