package com.bluepig.alarm.data.repository

import android.content.Context
import android.media.RingtoneManager
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.repository.RingtoneRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RingtoneRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val _context: Context,
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher
) : RingtoneRepository {
    private val _ringtoneManager
        get() = RingtoneManager(_context)

    override suspend fun getRingtoneList(): List<RingtoneMedia> =
        withContext(_dispatcher) {
            val result = mutableListOf<RingtoneMedia>()
            _ringtoneManager.apply {
                setType(RingtoneManager.TYPE_ALL)
                cursor.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val id = cursor.getLong(RingtoneManager.ID_COLUMN_INDEX)
                            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                            val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                            result.add(RingtoneMedia(id, title, uri))
                        } while (cursor.moveToNext())
                    }
                }
            }
            result
        }
}