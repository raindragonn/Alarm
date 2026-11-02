package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.data.network.response.MusicInfoResponse
import com.bluepig.alarm.domain.entity.music.MusicInfo

object MusicInfoMapper : BaseMapper<MusicInfoResponse, MusicInfo> {
    override fun mapToEntity(data: MusicInfoResponse): MusicInfo =
        MusicInfo(
            downloadPage = data.d1PageUrl,
            id = data.fileId,
            thumbnail = data.thumbnailUrl,
            title = data.fileName.removeExtension()
        )

    override fun mapToData(entity: MusicInfo): MusicInfoResponse =
        throw TypeCastException()

    private fun String.removeExtension(): String {
        val lastDotIndex = this.lastIndexOf(".")
        return if (lastDotIndex > 0) {
            substring(0, lastDotIndex)
        } else {
            this
        }
    }
}