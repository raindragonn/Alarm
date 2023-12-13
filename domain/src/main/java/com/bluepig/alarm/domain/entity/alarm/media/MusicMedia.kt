package com.bluepig.alarm.domain.entity.alarm.media

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class MusicMedia(
    override val title: String,
    val id: String,
    val thumbnail: String,
    val fileUrl: String
) : BaseEntity, AlarmMedia, java.io.Serializable
