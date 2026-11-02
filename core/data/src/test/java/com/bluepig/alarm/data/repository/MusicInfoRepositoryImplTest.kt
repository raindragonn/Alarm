package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.fake.FakeMusicRemoteDataSource
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.NetworkUnavailableException
import com.bluepig.alarm.network.response.MetaInfoResponse
import com.bluepig.alarm.network.response.MusicInfoResponse
import com.bluepig.alarm.network.response.SearchResponse
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.UnknownHostException
import java.util.Locale

class MusicInfoRepositoryImplTest {
    private val defaultLocale = Locale.getDefault()

    @After
    fun tearDown() = Locale.setDefault(defaultLocale)

    private val response = SearchResponse(
        musicInfoRespons = listOf(
            MusicInfoResponse(
                category = 1,
                d1PageUrl = "https://example.com/d1/aB3dE5fG",
                fileId = "aB3dE5fG",
                fileName = "아침 노래.mp3",
                fileSize = 1024,
                metaInfoResponse = MetaInfoResponse("320", "180", "", "", "2020"),
                tags = listOf("morning"),
                thumbnailUrl = "https://example.com/thumb.jpg",
                uploadTime = "2020-01-01",
            ),
        ),
        totalCount = 1,
    )

    @Test
    fun `검색 응답을 MusicInfo 목록으로 바꾼다`() = runTest {
        Locale.setDefault(Locale.KOREA)
        val remote = FakeMusicRemoteDataSource(response)

        val result = MusicInfoRepositoryImpl(remote).getFileList("아침", 20)

        val expected = MusicInfo(
            downloadPage = "https://example.com/d1/aB3dE5fG",
            id = "aB3dE5fG",
            thumbnail = "https://example.com/thumb.jpg",
            title = "아침 노래",
        )
        assertEquals(listOf(expected), result)
        assertEquals(listOf("ko"), remote.searchedLocales)
    }

    @Test
    fun `한국어가 아닌 로케일은 en으로 검색한다`() = runTest {
        Locale.setDefault(Locale.US)
        val remote = FakeMusicRemoteDataSource()

        MusicInfoRepositoryImpl(remote).getFileList("morning", 0)

        assertEquals(listOf("en"), remote.searchedLocales)
    }

    @Test
    fun `네트워크 예외는 domain 예외로 바꾼다`() = runTest {
        val remote = FakeMusicRemoteDataSource().apply { error = UnknownHostException() }

        val result = runCatching { MusicInfoRepositoryImpl(remote).getFileUrl("https://example.com") }

        assertTrue(result.exceptionOrNull() is NetworkUnavailableException)
    }
}
