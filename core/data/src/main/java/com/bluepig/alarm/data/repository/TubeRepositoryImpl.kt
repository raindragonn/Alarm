package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapDataErrors
import com.bluepig.alarm.data.mapper.toDomain
import com.bluepig.alarm.datastore.PreferencesLocalDataSource
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository
import com.bluepig.alarm.network.TubeRemoteDataSource
import javax.inject.Inject

internal class TubeRepositoryImpl @Inject constructor(
    private val _remoteDataSource: TubeRemoteDataSource,
    private val _preferences: PreferencesLocalDataSource,
) : TubeRepository {

    override suspend fun getTubeList(
        query: String,
        nextPageToken: String?
    ): Pair<List<TubeMedia>, String?> = mapDataErrors {
        val response = _remoteDataSource.search(selectedAccount(), query, nextPageToken)
        response.items.map { it.toDomain() } to response.nextPageToken
    }

    override suspend fun checkTubeMedia(query: String): TubeMedia? {
        val videoId = VIDEO_ID_REGEX.find(query)?.groupValues?.get(1) ?: return null
        return mapDataErrors {
            _remoteDataSource.getVideo(selectedAccount(), videoId)?.toDomain()
        }
    }

    override suspend fun getAccountName(): String? = _preferences.getSelectedAccountName()

    override suspend fun setAccountName(name: String) = _preferences.setSelectedAccountName(name)

    // 재인증 중에 ""로 지운 계정은 넘기지 않음 → credential에 남은 계정으로 요청 (계층 분리 전 화면과 같음)
    private suspend fun selectedAccount() =
        _preferences.getSelectedAccountName()?.takeIf { it.isNotBlank() }

    private companion object {
        val VIDEO_ID_REGEX = Regex("(?:youtu\\.be/|youtube\\.com/watch\\?v=)([^\"&?\\s/]{11})")
    }
}
