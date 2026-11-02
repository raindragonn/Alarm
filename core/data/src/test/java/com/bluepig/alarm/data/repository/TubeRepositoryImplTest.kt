package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.fake.FakePreferencesLocalDataSource
import com.bluepig.alarm.data.fake.FakeTubeRemoteDataSource
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.result.DataSourceException
import com.google.api.services.youtube.model.ResourceId
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.SearchResultSnippet
import com.google.api.services.youtube.model.Thumbnail
import com.google.api.services.youtube.model.ThumbnailDetails
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class TubeRepositoryImplTest {
    private val thumbnails =
        ThumbnailDetails().setDefault(Thumbnail().setUrl("https://i.ytimg.com/default.jpg"))
    private val tubeMedia = TubeMedia("abcdefghijk", "https://i.ytimg.com/default.jpg", "기상 음악")

    @Test
    fun `검색 결과와 다음 페이지 토큰을 돌려준다`() = runTest {
        val response = SearchListResponse()
            .setItems(
                listOf(
                    SearchResult()
                        .setId(ResourceId().setVideoId("abcdefghijk"))
                        .setSnippet(SearchResultSnippet().setTitle("기상 음악").setThumbnails(thumbnails)),
                ),
            )
            .setNextPageToken("next")
        val repository = TubeRepositoryImpl(FakeTubeRemoteDataSource(response), FakePreferencesLocalDataSource())

        assertEquals(listOf(tubeMedia) to "next", repository.getTubeList("wake up"))
    }

    @Test
    fun `링크에서 영상 id를 꺼내 영상 정보를 가져온다`() = runTest {
        val video = Video().setId("abcdefghijk")
            .setSnippet(VideoSnippet().setTitle("기상 음악").setThumbnails(thumbnails))
        val remote = FakeTubeRemoteDataSource(videos = mapOf("abcdefghijk" to video))
        val repository = TubeRepositoryImpl(remote, FakePreferencesLocalDataSource())

        assertEquals(tubeMedia, repository.checkTubeMedia("https://youtu.be/abcdefghijk"))
        assertEquals(tubeMedia, repository.checkTubeMedia("https://www.youtube.com/watch?v=abcdefghijk&t=3"))
        assertNull(repository.checkTubeMedia("기상 음악"))
        assertEquals(listOf("abcdefghijk", "abcdefghijk"), remote.requestedVideoIds)
    }

    @Test
    fun `저장된 계정으로 요청하고 비어 있으면 넘기지 않는다`() = runTest {
        val remote = FakeTubeRemoteDataSource()
        val preferences = FakePreferencesLocalDataSource(accountName = "user@example.com")
        val repository = TubeRepositoryImpl(remote, preferences)

        repository.getTubeList("wake up")
        repository.setAccountName("")
        repository.getTubeList("wake up")

        assertEquals(listOf("user@example.com", null), remote.accountNames)
        assertEquals("", repository.getAccountName())
    }

    @Test
    fun `YouTube API 오류는 domain 예외로 바꾼다`() = runTest {
        val remote = FakeTubeRemoteDataSource().apply { error = IOException("403") }

        val result = runCatching {
            TubeRepositoryImpl(remote, FakePreferencesLocalDataSource()).getTubeList("wake up")
        }

        assertTrue(result.exceptionOrNull() is DataSourceException)
    }
}
