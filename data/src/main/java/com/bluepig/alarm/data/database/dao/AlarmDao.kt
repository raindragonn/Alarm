package com.bluepig.alarm.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluepig.alarm.data.database.data.AlarmData
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmData): Long

    @Update
    suspend fun update(alarm: AlarmData)

    @Query("DELETE FROM alarm WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getById(id: Long): AlarmData?

    @Query("SELECT * FROM alarm")
    fun getAllFlow(): Flow<List<AlarmData>>

    @Query("SELECT * FROM alarm")
    fun getAllAlarms(): List<AlarmData>
}