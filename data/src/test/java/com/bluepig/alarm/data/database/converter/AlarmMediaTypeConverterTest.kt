package com.bluepig.alarm.data.database.converter

import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import org.junit.Assert.assertEquals
import org.junit.Test

// 배포본이 alarm.alarmMedia 컬럼에 저장한 JSON. 이 문자열을 바꾸면 기존 사용자 알람을 못 읽는다.
class AlarmMediaTypeConverterTest {
    private val converter = AlarmMediaTypeConverter()

    private fun assertGolden(json: String, media: AlarmMedia) {
        assertEquals(media, converter.jsonStringToAlarmMedia(json))
        assertEquals(json, converter.alarmMediaToJsonString(media))
    }

    @Test
    fun `MusicMedia`() = assertGolden(
        """{"type":"com.bluepig.alarm.domain.entity.alarm.media.MusicMedia","title":"아침 노래","id":"aB3dE5fG","thumbnail":"https://example.com/thumb.jpg","fileUrl":"https://example.com/song.mp3"}""",
        MusicMedia(
            title = "아침 노래",
            id = "aB3dE5fG",
            thumbnail = "https://example.com/thumb.jpg",
            fileUrl = "https://example.com/song.mp3",
        ),
    )

    @Test
    fun `TubeMedia`() = assertGolden(
        """{"type":"com.bluepig.alarm.domain.entity.alarm.media.TubeMedia","videoId":"abcdefghijk","thumbnail":"https://i.ytimg.com/vi/abcdefghijk/default.jpg","title":"기상 음악"}""",
        TubeMedia(
            videoId = "abcdefghijk",
            thumbnail = "https://i.ytimg.com/vi/abcdefghijk/default.jpg",
            title = "기상 음악",
        ),
    )

    @Test
    fun `RingtoneMedia`() = assertGolden(
        """{"type":"com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia","id":12,"title":"Beep","uri":"content://media/internal/audio/media/12"}""",
        RingtoneMedia(id = 12, title = "Beep", uri = "content://media/internal/audio/media/12"),
    )
}
