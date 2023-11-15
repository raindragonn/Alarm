package com.bluepig.alarm.domain.entity.file

import com.bluepig.alarm.domain.entity.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
data class File(
    val downloadPage: String,
    val id: String,
    val thumbnail: String,
    val title: String,
) : BaseEntity
