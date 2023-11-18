package com.bluepig.alarm.manager.download

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

@UnstableApi
class MediaDownloadManagerImpl @Inject constructor(
    private val _context: Context
) : MediaDownloadManager {

    override fun getDownloadManager(): DownloadManager {
        return DownloadManager(
            _context,
            getDatabaseProvider(),
            getDownloadCache(),
            getDataSourceFactory(),
            Executor(Runnable::run)
        )
    }

    private fun getDatabaseProvider(): DatabaseProvider {
        return StandaloneDatabaseProvider(_context)
    }

    private fun getDownloadCache(): Cache {
        val downloadDirectory = File(
            _context.getExternalFilesDir(null),
            MediaDownloadConst.DOWNLOAD_DIRECTORY
        )
        return SimpleCache(
            downloadDirectory,
            NoOpCacheEvictor(),
            getDatabaseProvider()
        )
    }

    /**
     * Get data source factory
     *
     * 캐시를 이용하는 데이터 소스 팩토리.
     * 데이터가 캐시되지 않은 경우 HttpDataSource를 이용해 요청된다.
     *
     */
    private fun getDataSourceFactory(): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(getDownloadCache())
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}