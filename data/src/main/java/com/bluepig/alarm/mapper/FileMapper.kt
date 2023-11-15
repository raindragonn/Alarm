package com.bluepig.alarm.mapper

import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.network.response.FileResponse

object FileMapper : BaseMapper<FileResponse, File> {
    override fun mapToEntity(data: FileResponse): File =
        File(
            downloadPage = data.d1PageUrl,
            id = data.fileId
        )

    override fun mapToData(entity: File): FileResponse =
        throw TypeCastException()
}