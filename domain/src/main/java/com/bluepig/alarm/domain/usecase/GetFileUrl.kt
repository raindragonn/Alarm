package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetFileUrl @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _fileRepository: FileRepository,
) {
    suspend operator fun invoke(pageUrl: String, userAgent: String) =
        asyncResultWithContextOf(_dispatcher) {
            _fileRepository.getFileUrl(pageUrl, userAgent)
        }
}