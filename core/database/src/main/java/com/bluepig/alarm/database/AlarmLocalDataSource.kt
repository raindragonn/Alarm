package com.bluepig.alarm.database

import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {
    fun getAllFlow(): Flow<List<AlarmData>>
    suspend fun getAll(): List<AlarmData>
    suspend fun getById(id: Long): AlarmData?

    /** id가 같은 알람이 있으면 덮어씀 */
    suspend fun insert(alarm: AlarmData): Long
    suspend fun deleteById(id: Long)
}

internal class AlarmLocalDataSourceImpl(
    private val _dao: AlarmDao,
) : AlarmLocalDataSource {
    override fun getAllFlow() = _dao.getAllFlow()
    override suspend fun getAll() = _dao.getAll()
    override suspend fun getById(id: Long) = _dao.getById(id)
    override suspend fun insert(alarm: AlarmData) = _dao.insert(alarm)
    override suspend fun deleteById(id: Long) = _dao.deleteById(id)
}
