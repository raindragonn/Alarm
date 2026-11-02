package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.music.MusicInfo

interface MusicInfoRepository {
    suspend fun getFileList(query: String, offSet: Int): List<MusicInfo>
    suspend fun getFileUrl(pageUrl: String, userAgent: String): String
}