package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.config.AppConfig

interface AppConfigRepository {
    suspend fun getConfig(): AppConfig
    suspend fun saveConfig(config: AppConfig)
    suspend fun getLastInterstitialShowTime(): Long
    suspend fun setLastInterstitialShowTime(timeInMillis: Long)
}
