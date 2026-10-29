package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.domain.result.runSuspendCatching
import javax.inject.Inject

/** 검색 결과의 다운로드 페이지에서 재생할 파일 주소를 얻는다 */
class GetMusicMedia @Inject constructor(
    private val _repository: MusicInfoRepository,
) {
    suspend operator fun invoke(musicInfo: MusicInfo): Result<MusicMedia> = runSuspendCatching {
        MusicMedia(
            id = musicInfo.id,
            thumbnail = musicInfo.thumbnail,
            title = musicInfo.title,
            fileUrl = _repository.getFileUrl(musicInfo.downloadPage)
        )
    }
}
