package com.bluepig.alarm.network

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import com.bluepig.alarm.network.response.SearchResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MusicRemoteDataSource {
    suspend fun search(query: String, offset: Int, locale: String): SearchResponse

    /** 다운로드 페이지를 열어 재생할 파일 주소를 꺼냄 */
    suspend fun getFileUrl(pageUrl: String): String
}

internal class MusicRemoteDataSourceImpl @Inject constructor(
    @ApplicationContext private val _context: Context,
    private val _ioDispatcher: CoroutineDispatcher,
    private val _searchApi: SearchApi,
    private val _pageParser: MusicInfoPageParser,
) : MusicRemoteDataSource {

    override suspend fun search(query: String, offset: Int, locale: String): SearchResponse =
        _searchApi.getSongList(query = query, offset = offset, locale = locale)

    override suspend fun getFileUrl(pageUrl: String): String = withContext(_ioDispatcher) {
        _pageParser.parse(pageUrl, userAgent())
    }

    // 계층 분리 전 app이 Media3로 만들던 것과 같은 문자열
    @OptIn(UnstableApi::class)
    private fun userAgent() = Util.getUserAgent(_context, _context.getString(R.string.app_name))
}
