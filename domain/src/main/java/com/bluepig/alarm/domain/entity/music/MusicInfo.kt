package com.bluepig.alarm.domain.entity.music

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class MusicInfo(
    val downloadPage: String,
    val id: String,
    val thumbnail: String,
    val title: String,
) : BaseEntity, java.io.Serializable
