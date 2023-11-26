package com.bluepig.alarm.domain.entity.file

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class SongFile(
    override val downloadPage: String,
    override val id: String,
    override val thumbnail: String,
    override val title: String,
    val fileUrl: String
) : File, BaseEntity, java.io.Serializable
