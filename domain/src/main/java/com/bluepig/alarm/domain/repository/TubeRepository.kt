package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia

interface TubeRepository {
    suspend fun getTubeList(
        query: String,
        nextPageToken: String? = null
    ): Pair<List<TubeMedia>, String>

    suspend fun checkTubeMedia(query: String): TubeMedia?
}