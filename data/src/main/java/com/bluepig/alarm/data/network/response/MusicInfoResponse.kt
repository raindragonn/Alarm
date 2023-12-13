package com.bluepig.alarm.data.network.response


import androidx.annotation.Keep
import com.bluepig.alarm.data.database.data.BaseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MusicInfoResponse(
    @SerialName("category")
    val category: Int,
    @SerialName("d1PageUrl")
    val d1PageUrl: String,
    @SerialName("fileId")
    val fileId: String,
    @SerialName("fileName")
    val fileName: String,
    @SerialName("fileSize")
    val fileSize: Int,
    @SerialName("metaInfo")
    val metaInfoResponse: MetaInfoResponse,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("uploadTime")
    val uploadTime: String,
) : BaseData