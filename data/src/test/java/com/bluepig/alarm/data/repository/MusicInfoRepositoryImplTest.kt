package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.di.NetworkModule
import com.bluepig.alarm.data.network.parser.MusicInfoPageParser
import com.bluepig.alarm.domain.entity.music.MusicInfo
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class MusicInfoRepositoryImplTest {
    private val server = MockWebServer()
    private val defaultLocale = Locale.getDefault()

    @After
    fun tearDown() {
        server.shutdown()
        Locale.setDefault(defaultLocale)
    }

    // NetworkModule의 Retrofit 설정을 그대로 쓰고, api.keys의 host만 가짜 서버로 바꾼다
    private fun TestScope.repository(): MusicInfoRepositoryImpl {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .scheme("http")
                    .host(server.hostName)
                    .port(server.port)
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()
        val searchApi = NetworkModule.providesSearchApi(NetworkModule.providesRetrofit(client))
        val unusedParser = object : MusicInfoPageParser {
            override suspend fun parse(url: String, userAgent: String) = error("사용 안 함")
        }
        return MusicInfoRepositoryImpl(StandardTestDispatcher(testScheduler), searchApi, unusedParser)
    }

    @Test
    fun `검색 응답을 MusicInfo 목록으로 바꾼다`() = runTest {
        Locale.setDefault(Locale.KOREA)
        server.enqueue(MockResponse().setBody(SEARCH_RESPONSE))

        val result = repository().getFileList("아침", 20)

        val expected = MusicInfo(
            downloadPage = "https://example.com/d1/aB3dE5fG",
            id = "aB3dE5fG",
            thumbnail = "https://example.com/thumb.jpg",
            title = "아침 노래",
        )
        assertEquals(listOf(expected), result)
        val url = server.takeRequest().requestUrl!!
        assertEquals("아침", url.queryParameter("query"))
        assertEquals("20", url.queryParameter("offset"))
        assertEquals("ko", url.queryParameter("locale"))
    }

    @Test
    fun `한국어가 아닌 로케일은 en으로 검색한다`() = runTest {
        Locale.setDefault(Locale.US)
        server.enqueue(MockResponse().setBody("""{"items":[],"totalCount":0}"""))

        repository().getFileList("morning", 0)

        assertEquals("en", server.takeRequest().requestUrl!!.queryParameter("locale"))
    }

    companion object {
        // 모르는 필드(downloadCount)는 무시해야 한다
        private const val SEARCH_RESPONSE = """
            {"items":[{"category":1,"d1PageUrl":"https://example.com/d1/aB3dE5fG",
            "fileId":"aB3dE5fG","fileName":"아침 노래.mp3","fileSize":1024,
            "metaInfo":{"bitRate":"320","duration":"180","resolution":"","ringtoneLink":"",
            "year":"2020"},"tags":["morning"],"thumbnailUrl":"https://example.com/thumb.jpg",
            "uploadTime":"2020-01-01","downloadCount":3}],"totalCount":1}
        """
    }
}
