package com.bluepig.alarm.network

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.Video
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** [accountName]이 null이면 credential에 이미 고른 계정으로 요청함 */
interface TubeRemoteDataSource {
    suspend fun search(accountName: String?, query: String, pageToken: String?): SearchListResponse
    suspend fun getVideo(accountName: String?, videoId: String): Video?
}

internal class TubeRemoteDataSourceImpl @Inject constructor(
    private val _ioDispatcher: CoroutineDispatcher,
    private val _credential: GoogleAccountCredential,
    private val _youtube: YouTube,
) : TubeRemoteDataSource {

    override suspend fun search(
        accountName: String?,
        query: String,
        pageToken: String?
    ): SearchListResponse = withContext(_ioDispatcher) {
        selectAccount(accountName)
        _youtube.search().list(YOUTUBE_PARTS).apply {
            q = query
            type = listOf(TYPE)
            videoCategoryId = CATEGORY
            maxResults = MAX_RESULTS
            videoLicense = VIDEO_LICENSE
            pageToken?.let { this.pageToken = it }
        }.execute()
    }

    override suspend fun getVideo(accountName: String?, videoId: String): Video? =
        withContext(_ioDispatcher) {
            selectAccount(accountName)
            _youtube.videos().list(YOUTUBE_PARTS).apply {
                id = listOf(videoId)
            }.execute().items.firstOrNull()
        }

    private fun selectAccount(accountName: String?) {
        if (accountName != null) _credential.selectedAccountName = accountName
    }

    private companion object {
        val YOUTUBE_PARTS = listOf("id", "snippet")
        const val TYPE = "video"
        const val CATEGORY = "10"
        const val MAX_RESULTS = 50L
        const val VIDEO_LICENSE = "youtube"
    }
}
