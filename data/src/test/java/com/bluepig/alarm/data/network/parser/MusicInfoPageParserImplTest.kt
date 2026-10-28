package com.bluepig.alarm.data.network.parser

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class MusicInfoPageParserImplTest {
    private val server = MockWebServer()

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `다운로드 페이지에서 셀렉터로 고른 요소의 속성을 꺼낸다`() = runTest {
        server.enqueue(
            MockResponse()
                .setHeader("Content-Type", "text/html; charset=utf-8")
                .setBody(
                    """<html><body><input class="file" value="https://cdn.example.com/song.mp3"></body></html>""",
                ),
        )

        val fileUrl = MusicInfoPageParserImpl("input.file", "value")
            .parse(server.url("/d1/aB3dE5fG").toString(), "TestAgent/1.0")

        assertEquals("https://cdn.example.com/song.mp3", fileUrl)
        assertEquals("TestAgent/1.0", server.takeRequest().getHeader("User-Agent"))
    }
}
