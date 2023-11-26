package com.bluepig.alarm.domain.entity.file

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class BasicFile(
    override val downloadPage: String,
    override val id: String,
    override val thumbnail: String,
    override val title: String,
) : File, BaseEntity, java.io.Serializable
