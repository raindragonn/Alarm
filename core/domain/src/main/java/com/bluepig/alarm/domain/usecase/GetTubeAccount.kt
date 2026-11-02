package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.TubeRepository
import javax.inject.Inject

class GetTubeAccount @Inject constructor(
    private val _repository: TubeRepository,
) {
    suspend operator fun invoke(): String? = _repository.getAccountName()
}
