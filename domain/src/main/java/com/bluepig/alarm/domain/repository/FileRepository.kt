package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.file.File

interface FileRepository {
    suspend fun getFileList(query: String, offSet: Int): List<File>
}