package com.bluepig.alarm.data.mapper

import com.bluepig.alarm.data.network.response.FileResponse
import com.bluepig.alarm.domain.entity.file.BasicFile

object FileMapper : BaseMapper<FileResponse, BasicFile> {
    override fun mapToEntity(data: FileResponse): BasicFile =
        BasicFile(
            downloadPage = data.d1PageUrl,
            id = data.fileId,
            thumbnail = data.thumbnailUrl,
            title = data.fileName
        )

    override fun mapToData(entity: BasicFile): FileResponse =
        throw TypeCastException()
}