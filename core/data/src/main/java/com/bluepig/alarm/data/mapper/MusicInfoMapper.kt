package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.network.response.MusicInfoResponse

internal fun MusicInfoResponse.toDomain() = MusicInfo(
    downloadPage = d1PageUrl,
    id = fileId,
    thumbnail = thumbnailUrl,
    title = fileName.removeExtension(),
)

private fun String.removeExtension(): String {
    val lastDotIndex = lastIndexOf(".")
    return if (lastDotIndex > 0) {
        substring(0, lastDotIndex)
    } else {
        this
    }
}
