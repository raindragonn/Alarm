package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMusicMediaTest {
    @Test
    fun `다운로드 페이지에서 얻은 파일 주소로 MusicMedia를 만든다`() = runTest {
        val repository = object : MusicInfoRepository {
            override suspend fun getFileList(query: String, offSet: Int) = emptyList<MusicInfo>()
            override suspend fun getFileUrl(pageUrl: String) = "$pageUrl/song.mp3"
        }
        val info = MusicInfo("https://example.com/d1", "aB3dE5fG", "https://example.com/t.jpg", "아침 노래")

        val media = GetMusicMedia(repository)(info).getOrThrow()

        assertEquals(
            MusicMedia("아침 노래", "aB3dE5fG", "https://example.com/t.jpg", "https://example.com/d1/song.mp3"),
            media,
        )
    }
}
