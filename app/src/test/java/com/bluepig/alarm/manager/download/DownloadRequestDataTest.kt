package com.bluepig.alarm.manager.download

import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

// MediaDownloadManagerImpl이 Media3 다운로드 요청 data에 쓰는 JSON.
// 이미 받아 둔 다운로드 인덱스에 남아 있으므로 형식을 바꾸지 않는다.
class DownloadRequestDataTest {
    private val json =
        """{"title":"아침 노래","id":"aB3dE5fG","thumbnail":"https://example.com/thumb.jpg","fileUrl":"https://example.com/song.mp3"}"""
    private val media = MusicMedia(
        title = "아침 노래",
        id = "aB3dE5fG",
        thumbnail = "https://example.com/thumb.jpg",
        fileUrl = "https://example.com/song.mp3",
    )

    @Test
    fun `MusicMedia`() {
        assertEquals(media, Json.decodeFromString(MusicMedia.serializer(), json))
        assertEquals(json, Json.encodeToString(MusicMedia.serializer(), media))
    }
}
