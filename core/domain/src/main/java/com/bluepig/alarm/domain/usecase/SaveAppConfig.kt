package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.config.AppConfig
import com.bluepig.alarm.domain.repository.AppConfigRepository
import javax.inject.Inject

class SaveAppConfig @Inject constructor(
    private val _repository: AppConfigRepository,
) {
    suspend operator fun invoke(config: AppConfig) = _repository.saveConfig(config)
}
