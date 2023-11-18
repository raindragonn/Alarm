package com.bluepig.alarm.repository

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.mapper.FileMapper
import com.bluepig.alarm.network.api.SearchApi
import com.bluepig.alarm.network.parser.FilePageParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FileRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _searchApi: SearchApi,
    private val _filePageParser: FilePageParser
) : FileRepository {
    override suspend fun getFileList(query: String, offSet: Int): List<File> =
        withContext(_dispatcher) {
            val response = _searchApi.getSongList(
                query = query,
                offset = offSet,
            )

            response.fileResponses.map(FileMapper::mapToEntity)
        }

    override suspend fun getFileUrl(pageUrl: String, userAgent: String): String =
        withContext(_dispatcher) {
            _filePageParser.parse(pageUrl, userAgent)
        }
}