package com.bluepig.alarm.usecase

import android.content.Context
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.domain.usecase.GetMusicMedia
import com.bluepig.alarm.util.ext.userAgent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMusicMediaImpl @Inject constructor(
    @ApplicationContext
    private val _context: Context,
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _musicInfoRepository: MusicInfoRepository,
) : GetMusicMedia {
    private val _userAgent
        get() = _context.userAgent

    override suspend fun invoke(musicInfo: MusicInfo) =
        kotlin.runCatching {
            withContext(_dispatcher) {
                val pageUrl = musicInfo.downloadPage
                val fileUrl = _musicInfoRepository.getFileUrl(pageUrl, _userAgent)
                MusicMedia(
                    id = musicInfo.id,
                    thumbnail = musicInfo.thumbnail,
                    title = musicInfo.title,
                    fileUrl = fileUrl
                )
            }
        }

}