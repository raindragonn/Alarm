package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Video

internal fun SearchResult.toDomain() = TubeMedia(
    videoId = id.videoId,
    thumbnail = snippet.thumbnails.default.url,
    title = snippet.title,
)

internal fun Video.toDomain() = TubeMedia(
    videoId = id,
    thumbnail = snippet.thumbnails.default.url,
    title = snippet.title,
)
