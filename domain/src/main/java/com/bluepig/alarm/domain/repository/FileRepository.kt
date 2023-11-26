package com.bluepig.alarm.domain.repository

import com.bluepig.alarm.domain.entity.file.BasicFile

interface FileRepository {
    suspend fun getFileList(query: String, offSet: Int): List<BasicFile>
    suspend fun getFileUrl(pageUrl: String, userAgent: String): String
}