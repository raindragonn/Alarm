package com.bluepig.alarm.data.fake

import com.bluepig.alarm.network.TubeRemoteDataSource
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.Video

class FakeTubeRemoteDataSource(
    private val searchResponse: SearchListResponse = SearchListResponse().setItems(emptyList()),
    private val videos: Map<String, Video> = emptyMap(),
) : TubeRemoteDataSource {
    var error: Exception? = null
    val requestedVideoIds = mutableListOf<String>()

    override suspend fun search(query: String, pageToken: String?): SearchListResponse {
        error?.let { throw it }
        return searchResponse
    }

    override suspend fun getVideo(videoId: String): Video? {
        error?.let { throw it }
        requestedVideoIds += videoId
        return videos[videoId]
    }
}
