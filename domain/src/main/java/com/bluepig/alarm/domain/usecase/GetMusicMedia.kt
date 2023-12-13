package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo

interface GetMusicMedia {
    suspend operator fun invoke(musicInfo: MusicInfo): Result<MusicMedia>
}