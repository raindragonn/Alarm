package com.bluepig.alarm.manager.download

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.service.MediaDownloadService
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

@UnstableApi
class MediaDownloadManagerImpl @Inject constructor(
    private val _context: Context
) : MediaDownloadManager {

    private var _downloadManager: DownloadManager? = null
    private var _downloadCache: Cache? = null
    private var _datasourceFactory: DataSource.Factory? = null

    override fun getDownloadManager(): DownloadManager {
        return _downloadManager ?: DownloadManager(
            _context,
            getDatabaseProvider(),
            getDownloadCache(),
            getDataSourceFactory(),
            Executor(Runnable::run)
        ).also { _downloadManager = it }
    }

    override fun getDownloadNotificationHelper(): DownloadNotificationHelper {
        return DownloadNotificationHelper(
            _context,
            NotificationType.DOWNLOAD_NOTIFICATION.channelId
        )
    }

    private fun getDatabaseProvider(): DatabaseProvider {
        return StandaloneDatabaseProvider(_context)
    }

    private fun getDownloadCache(): Cache {
        val downloadDirectory = java.io.File(
            _context.getExternalFilesDir(null),
            MediaDownloadConst.DOWNLOAD_DIRECTORY
        )
        return _downloadCache ?: SimpleCache(
            downloadDirectory,
            NoOpCacheEvictor(),
            getDatabaseProvider()
        ).also {
            _downloadCache = it
        }
    }

    override fun getMediaItem(musicMedia: MusicMedia): MediaItem {
        val download = getDownloadManager().downloadIndex.getDownload(musicMedia.id)
        val downloadedUri = download?.request?.uri

        return downloadedUri?.let {
            MediaItem.fromUri(it)
        } ?: MediaItem.fromUri(musicMedia.fileUrl)
    }

    override fun startDownload(musicMedia: MusicMedia) {
        val mediaItem = getMediaItem(musicMedia)
        val downloadHelper = DownloadHelper.forMediaItem(
            _context,
            mediaItem
        )

        if (getDownloadManager().downloadIndex.getDownload(musicMedia.id) != null) return

        val jsonString =
            Json.encodeToString(MusicMedia.serializer(), musicMedia)
        val data = Util.getUtf8Bytes(jsonString)
        val request =
            downloadHelper.getDownloadRequest(
                musicMedia.id,
                data,
            )

        DownloadService.sendAddDownload(
            _context,
            MediaDownloadService::class.java,
            request,
            true
        )
    }

    /**
     * Get downloaded ids
     *
     * @return 현재 다운로드된 아이디 리스트를 반환한다.
     */
    private fun getDownloadedIds(): List<String> {
        val downloads = mutableListOf<Download>()
        getDownloadManager().downloadIndex
            .getDownloads().use {
                if (it.moveToFirst()) {
                    do {
                        downloads.add(it.download)
                    } while (it.moveToNext())
                }
            }
        return downloads.map { it.request.id }
    }

    override fun removeDownload(musicMedias: List<MusicMedia>) {
        val downloadedIds = getDownloadedIds().toSet()
        val filesIds = musicMedias.map { it.id }.toSet()
        downloadedIds.minus(filesIds)
            .also {
                Timber.d(it.toString())
            }
            .forEach {
                DownloadService.sendRemoveDownload(
                    _context,
                    MediaDownloadService::class.java,
                    it,
                    false
                )
            }
    }

    override fun getDataSourceFactory(): DataSource.Factory {
        return _datasourceFactory ?: CacheDataSource.Factory()
            .setCache(getDownloadCache())
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .also { _datasourceFactory = it }
    }
}