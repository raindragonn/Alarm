package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.TubeRepository
import javax.inject.Inject

class SaveTubeAccount @Inject constructor(
    private val _repository: TubeRepository,
) {
    suspend operator fun invoke(name: String) = _repository.setAccountName(name)
}
