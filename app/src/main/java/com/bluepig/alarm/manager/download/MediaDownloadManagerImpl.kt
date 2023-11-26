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
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.service.MediaDownloadService
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject
import com.bluepig.alarm.domain.entity.file.BasicFile as DomainFile

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

    override fun getMediaItem(url: String, id: String): MediaItem {
        val download = getDownloadManager().downloadIndex.getDownload(id)
        val downloadedUri = download?.request?.uri
        Timber.d("$downloadedUri")


        return downloadedUri?.let {
            MediaItem.fromUri(it)
        } ?: MediaItem.fromUri(url)
    }

    override fun startDownload(mediaItem: MediaItem, file: DomainFile) {
        val downloadHelper = DownloadHelper.forMediaItem(
            _context,
            mediaItem
        )
        val jsonString =
            Json.encodeToString(DomainFile.serializer(), file)
        val data = Util.getUtf8Bytes(jsonString)
        val request =
            downloadHelper.getDownloadRequest(
                file.id,
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
     * Get data source factory
     *
     * 캐시를 이용하는 데이터 소스 팩토리.
     * 데이터가 캐시되지 않은 경우 HttpDataSource를 이용해 요청된다.
     *
     */
    override fun getDataSourceFactory(): DataSource.Factory {
        return _datasourceFactory ?: CacheDataSource.Factory()
            .setCache(getDownloadCache())
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .also { _datasourceFactory = it }
    }
}