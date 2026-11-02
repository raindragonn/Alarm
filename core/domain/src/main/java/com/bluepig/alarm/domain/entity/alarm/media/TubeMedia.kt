package com.bluepig.alarm.domain.entity.alarm.media

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class TubeMedia(
    val videoId: String,
    val thumbnail: String,
    override val title: String,
) : BaseEntity, AlarmMedia, java.io.Serializable
