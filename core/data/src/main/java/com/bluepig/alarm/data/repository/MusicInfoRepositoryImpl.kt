package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapDataErrors
import com.bluepig.alarm.data.mapper.toDomain
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.network.MusicRemoteDataSource
import java.util.Locale
import javax.inject.Inject

internal class MusicInfoRepositoryImpl @Inject constructor(
    private val _remoteDataSource: MusicRemoteDataSource,
) : MusicInfoRepository {
    override suspend fun getFileList(query: String, offSet: Int): List<MusicInfo> =
        mapDataErrors {
            val locale = if (Locale.getDefault() == Locale.KOREA) {
                "ko"
            } else {
                "en"
            }
            _remoteDataSource.search(query = query, offset = offSet, locale = locale)
                .musicInfoRespons
                .map { it.toDomain() }
        }

    override suspend fun getFileUrl(pageUrl: String): String =
        mapDataErrors { _remoteDataSource.getFileUrl(pageUrl) }
}
