package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapper.toData
import com.bluepig.alarm.data.mapper.toDomain
import com.bluepig.alarm.database.AlarmLocalDataSource
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlarmRepositoryImpl @Inject constructor(
    private val _localDataSource: AlarmLocalDataSource,
) : AlarmRepository {

    override fun getAllAlarmFlow(): Flow<List<Alarm>> =
        _localDataSource.getAllFlow()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getAllAlarms(): List<Alarm> =
        _localDataSource.getAll().map { it.toDomain() }

    override suspend fun insertAlarm(alarm: Alarm): Long =
        _localDataSource.insert(alarm.toData())

    override suspend fun getById(id: Long): Alarm? =
        _localDataSource.getById(id)?.toDomain()

    override suspend fun deleteById(id: Long) =
        _localDataSource.deleteById(id)
}
