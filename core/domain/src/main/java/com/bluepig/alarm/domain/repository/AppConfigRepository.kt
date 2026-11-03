package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.config.AppConfig

interface AppConfigRepository {
    suspend fun getConfig(): AppConfig
    suspend fun saveConfig(config: AppConfig)
    suspend fun getLastInterstitialShowTime(): Long
    suspend fun setLastInterstitialShowTime(timeInMillis: Long)

    /** 목록의 일반 권한 안내 카드를 닫았는지 (제품 0017) */
    suspend fun isOverlayNoticeDismissed(): Boolean
    suspend fun dismissOverlayNotice()
}
