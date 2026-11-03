package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AppConfigRepository
import javax.inject.Inject

class DismissOverlayNotice @Inject constructor(
    private val _repository: AppConfigRepository,
) {
    suspend operator fun invoke() = _repository.dismissOverlayNotice()
}
