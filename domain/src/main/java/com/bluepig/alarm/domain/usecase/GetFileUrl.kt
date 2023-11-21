package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Get file url
 *
 * 노래 상세 페이지에서 음원파일 url 가져오기
 *
 * @property _dispatcher
 * @property _fileRepository
 * @constructor Create empty Get file url
 */
class GetFileUrl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _fileRepository: FileRepository,
) {
    suspend operator fun invoke(file: File, userAgent: String) =
        asyncResultWithContextOf(_dispatcher) {
            val pageUrl = file.downloadPage
            val fileUrl = _fileRepository.getFileUrl(pageUrl, userAgent)
            file.id to fileUrl
        }
}