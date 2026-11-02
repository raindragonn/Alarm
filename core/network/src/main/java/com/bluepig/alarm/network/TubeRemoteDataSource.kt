package com.bluepig.alarm.network

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.Video
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface TubeRemoteDataSource {
    suspend fun search(query: String, pageToken: String?): SearchListResponse
    suspend fun getVideo(videoId: String): Video?
}

internal class TubeRemoteDataSourceImpl @Inject constructor(
    private val _ioDispatcher: CoroutineDispatcher,
    private val _youtube: YouTube,
) : TubeRemoteDataSource {

    override suspend fun search(
        query: String,
        pageToken: String?
    ): SearchListResponse = withContext(_ioDispatcher) {
        _youtube.search().list(YOUTUBE_PARTS).apply {
            q = query
            type = listOf(TYPE)
            videoCategoryId = CATEGORY
            maxResults = MAX_RESULTS
            videoLicense = VIDEO_LICENSE
            pageToken?.let { this.pageToken = it }
        }.execute()
    }

    override suspend fun getVideo(videoId: String): Video? =
        withContext(_ioDispatcher) {
            _youtube.videos().list(YOUTUBE_PARTS).apply {
                id = listOf(videoId)
            }.execute().items.firstOrNull()
        }

    private companion object {
        val YOUTUBE_PARTS = listOf("id", "snippet")
        const val TYPE = "video"
        const val CATEGORY = "10"
        const val MAX_RESULTS = 50L
        const val VIDEO_LICENSE = "youtube"
    }
}
