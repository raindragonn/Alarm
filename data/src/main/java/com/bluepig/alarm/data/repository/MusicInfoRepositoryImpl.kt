package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapper.MusicInfoMapper
import com.bluepig.alarm.data.network.api.SearchApi
import com.bluepig.alarm.data.network.parser.MusicInfoPageParser
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject


class MusicInfoRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _searchApi: SearchApi,
    private val _musicInfoPageParser: MusicInfoPageParser
) : MusicInfoRepository {
    override suspend fun getFileList(query: String, offSet: Int): List<MusicInfo> =
        withContext(_dispatcher) {
            val locale = if (Locale.getDefault() == Locale.KOREA) {
                "ko"
            } else {
                "en"
            }
            val response = _searchApi.getSongList(
                query = query,
                offset = offSet,
                locale = locale
            )

            response.musicInfoRespons.map(MusicInfoMapper::mapToEntity)
        }

    override suspend fun getFileUrl(pageUrl: String, userAgent: String): String =
        withContext(_dispatcher) {
            _musicInfoPageParser.parse(pageUrl, userAgent)
        }
}