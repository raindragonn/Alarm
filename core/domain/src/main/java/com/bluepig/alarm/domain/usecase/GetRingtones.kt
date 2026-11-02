package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.RingtoneRepository
import com.bluepig.alarm.domain.result.runSuspendCatching
import javax.inject.Inject

class GetRingtones @Inject constructor(
    private val _repository: RingtoneRepository,
) {
    suspend operator fun invoke() = runSuspendCatching {
        _repository.getRingtoneList()
    }
}
