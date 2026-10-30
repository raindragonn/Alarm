package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository

class FakeMusicInfoRepository(
    private val files: List<MusicInfo> = emptyList(),
    private val fileUrl: String = "https://example.com/file.mp3",
    var error: Throwable? = null,
) : MusicInfoRepository {
    override suspend fun getFileList(query: String, offSet: Int): List<MusicInfo> {
        error?.let { throw it }
        return files.drop(offSet)
    }

    override suspend fun getFileUrl(pageUrl: String) = fileUrl
}
