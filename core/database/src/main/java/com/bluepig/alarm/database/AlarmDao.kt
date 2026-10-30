package com.bluepig.alarm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmData): Long

    @Query("DELETE FROM alarm WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getById(id: Long): AlarmData?

    @Query("SELECT * FROM alarm")
    fun getAllFlow(): Flow<List<AlarmData>>

    @Query("SELECT * FROM alarm")
    suspend fun getAll(): List<AlarmData>
}
