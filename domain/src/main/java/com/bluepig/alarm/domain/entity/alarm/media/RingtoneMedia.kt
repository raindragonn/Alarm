package com.bluepig.alarm.domain.entity.alarm.media

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class RingtoneMedia(
    val id: Long,
    override val title: String,
    val uri: String,
) : BaseEntity, AlarmMedia, java.io.Serializable
