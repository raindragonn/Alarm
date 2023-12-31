package com.bluepig.alarm.data.network.response


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SearchResponse(
    @SerialName("items")
    val musicInfoRespons: List<MusicInfoResponse>,
    @SerialName("totalCount")
    val totalCount: Int
)