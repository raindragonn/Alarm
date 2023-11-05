package com.bluepig.alarm.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bluepig.alarm.database.data.AlarmData

interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmData): Int

    @Update
    suspend fun update(alarm: AlarmData)

    @Query("DELETE FROM alarm WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getById(id: Int)
}