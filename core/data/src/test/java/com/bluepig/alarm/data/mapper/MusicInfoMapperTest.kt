package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.network.response.MetaInfoResponse
import com.bluepig.alarm.network.response.MusicInfoResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class MusicInfoMapperTest {
    private fun title(fileName: String) = MusicInfoResponse(
        category = 1,
        d1PageUrl = "https://example.com/d1/aB3dE5fG",
        fileId = "aB3dE5fG",
        fileName = fileName,
        fileSize = 1024,
        metaInfoResponse = MetaInfoResponse("320", "180", "", "", "2020"),
        tags = emptyList(),
        thumbnailUrl = "https://example.com/thumb.jpg",
        uploadTime = "2020-01-01",
    ).toDomain().title

    @Test
    fun `제목은 파일 이름에서 마지막 확장자만 뗀다`() {
        assertEquals("아침 노래", title("아침 노래.mp3"))
        assertEquals("a.b", title("a.b.mp3"))
        assertEquals("확장자 없음", title("확장자 없음"))
        assertEquals(".mp3", title(".mp3"))
    }
}
