package com.bluepig.alarm.domain.entity.file

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class File(
    @SerialName("downloadPage")
    val downloadPage: String,
    @SerialName("id")
    val id: String,
)
