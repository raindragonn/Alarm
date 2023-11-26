package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.mapper.FileMapper
import com.bluepig.alarm.data.network.api.SearchApi
import com.bluepig.alarm.data.network.parser.FilePageParser
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.file.BasicFile
import com.bluepig.alarm.domain.repository.FileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FileRepositoryImpl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _searchApi: SearchApi,
    private val _filePageParser: FilePageParser
) : FileRepository {
    override suspend fun getFileList(query: String, offSet: Int): List<BasicFile> =
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