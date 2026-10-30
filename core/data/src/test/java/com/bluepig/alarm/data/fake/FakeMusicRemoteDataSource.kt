package com.bluepig.alarm.data.fake

import com.bluepig.alarm.network.MusicRemoteDataSource
import com.bluepig.alarm.network.response.SearchResponse

class FakeMusicRemoteDataSource(
    private val response: SearchResponse = SearchResponse(emptyList(), 0),
    private val fileUrl: String = "",
) : MusicRemoteDataSource {
    var error: Exception? = null
    val searchedLocales = mutableListOf<String>()

    override suspend fun search(query: String, offset: Int, locale: String): SearchResponse {
        error?.let { throw it }
        searchedLocales += locale
        return response
    }

    override suspend fun getFileUrl(pageUrl: String): String {
        error?.let { throw it }
        return fileUrl
    }
}
