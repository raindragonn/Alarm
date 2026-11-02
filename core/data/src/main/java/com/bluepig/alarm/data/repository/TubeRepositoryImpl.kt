package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapDataErrors
import com.bluepig.alarm.data.mapper.toDomain
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository
import com.bluepig.alarm.network.TubeRemoteDataSource
import javax.inject.Inject

internal class TubeRepositoryImpl @Inject constructor(
    private val _remoteDataSource: TubeRemoteDataSource,
) : TubeRepository {

    override suspend fun getTubeList(
        query: String,
        nextPageToken: String?
    ): Pair<List<TubeMedia>, String?> = mapDataErrors {
        val response = _remoteDataSource.search(query, nextPageToken)
        response.items.map { it.toDomain() } to response.nextPageToken
    }

    override suspend fun checkTubeMedia(query: String): TubeMedia? {
        val videoId = VIDEO_ID_REGEX.find(query)?.groupValues?.get(1) ?: return null
        return mapDataErrors {
            _remoteDataSource.getVideo(videoId)?.toDomain()
        }
    }

    private companion object {
        val VIDEO_ID_REGEX = Regex("(?:youtu\\.be/|youtube\\.com/watch\\?v=)([^\"&?\\s/]{11})")
    }
}
