package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AppConfigRepository
import com.bluepig.alarm.domain.util.CalendarHelper
import javax.inject.Inject

class MarkInterstitialShown @Inject constructor(
    private val _repository: AppConfigRepository,
) {
    suspend operator fun invoke() =
        _repository.setLastInterstitialShowTime(CalendarHelper.now.timeInMillis)
}
