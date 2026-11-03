package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AppConfigRepository
import javax.inject.Inject

/** 목록의 일반 권한 안내 카드를 닫았는지 (제품 0017) */
class IsOverlayNoticeDismissed @Inject constructor(
    private val _repository: AppConfigRepository,
) {
    suspend operator fun invoke() = _repository.isOverlayNoticeDismissed()
}
