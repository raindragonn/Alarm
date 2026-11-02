package com.bluepig.alarm.network

import com.bluepig.alarm.network.response.MetaInfoResponse
import com.bluepig.alarm.network.response.MusicInfoResponse
import com.bluepig.alarm.network.response.SearchResponse
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchApiTest {
    private val server = MockWebServer()

    @After
    fun tearDown() = server.shutdown()

    // NetworkModule의 Retrofit 설정을 그대로 쓰고, api.keys의 host만 가짜 서버로 바꾼다
    private fun searchApi(): SearchApi {
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
        return NetworkModule.providesSearchApi(NetworkModule.providesRetrofit(client))
    }

    @Test
    fun `검색 응답을 읽고 검색어·offset·locale을 쿼리로 보낸다`() = runTest {
        server.enqueue(MockResponse().setBody(SEARCH_RESPONSE))

        val response = searchApi().getSongList(query = "아침", offset = 20, locale = "ko")

        val expected = SearchResponse(
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
        assertEquals(expected, response)
        val url = server.takeRequest().requestUrl!!
        assertEquals("아침", url.queryParameter("query"))
        assertEquals("20", url.queryParameter("offset"))
        assertEquals("ko", url.queryParameter("locale"))
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
