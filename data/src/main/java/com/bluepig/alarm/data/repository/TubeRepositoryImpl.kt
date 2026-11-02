package com.bluepig.alarm.data.repository

import android.content.Context
import com.bluepig.alarm.data.R
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TubeRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val _context: Context,
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _credential: GoogleAccountCredential,
) : TubeRepository {
    private val _youtube: YouTube by lazy {
        YouTube.Builder(
            NetHttpTransport(), JacksonFactory.getDefaultInstance(), _credential
        ).setApplicationName(_context.getString(R.string.app_name))
            .build()
    }


    override suspend fun getTubeList(
        query: String,
        nextPageToken: String?
    ): Pair<List<TubeMedia>, String> =
        withContext(_dispatcher) {
            val searchResponse = _youtube.search().list(YOUTUBE_PARTS).apply {
                q = query
                type = TYPE
                videoCategoryId = CATEGORY
                maxResults = MAX_RESULTS
                videoLicense = VIDEO_LICENSE
                nextPageToken?.let {
                    pageToken = it
                }
            }.execute()

            val mediaList = searchResponse.items.map { result ->
                TubeMedia(
                    videoId = result.id.videoId,
                    thumbnail = result.snippet.thumbnails.default.url,
                    title = result.snippet.title
                )
            }
            mediaList to searchResponse.nextPageToken
        }

    override suspend fun checkTubeMedia(query: String): TubeMedia? = withContext(_dispatcher) {
        val regex = Regex("(?:youtu\\.be/|youtube\\.com/watch\\?v=)([^\"&?\\s/]{11})")
        val matchResult = regex.find(query)
        val videoId = matchResult?.groupValues?.get(1) ?: return@withContext null

        val response = _youtube.videos().list(YOUTUBE_PARTS).apply {
            id = videoId
        }.execute()
        val item = response.items.firstOrNull() ?: return@withContext null

        return@withContext TubeMedia(
            videoId = videoId,
            thumbnail = item.snippet.thumbnails.default.url,
            title = item.snippet.title
        )
    }

    companion object {
        private val YOUTUBE_PARTS = listOf("id", "snippet").joinToString()
        private const val TYPE = "video"
        private const val CATEGORY = "10"
        private const val MAX_RESULTS = 50L
        private const val VIDEO_LICENSE = "youtube"
    }
}