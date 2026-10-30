package com.bluepig.alarm.network.response


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MetaInfoResponse(
    @SerialName("bitRate")
    val bitRate: String,
    @SerialName("duration")
    val duration: String,
    @SerialName("resolution")
    val resolution: String,
    @SerialName("ringtoneLink")
    val ringtoneLink: String,
    @SerialName("year")
    val year: String
)