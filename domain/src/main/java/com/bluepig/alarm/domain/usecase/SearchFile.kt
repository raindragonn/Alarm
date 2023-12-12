package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.file.BasicFile
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.domain.result.SearchQueryEmptyException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchFile @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: FileRepository
) {
    private var _lastQuery = ""
    private var _lastOffset = 0
    private var _cachedList = mutableListOf<BasicFile>()
    suspend operator fun invoke(query: String = ""): Result<List<BasicFile>> {
        val checkedQuery = checkSameQuery(query)

        return kotlin.runCatching {
            withContext(_dispatcher) {
                if (checkedQuery.isBlank()) throw SearchQueryEmptyException
                val result = _repository.getFileList(checkedQuery, _lastOffset)
                _cachedList.apply {
                    addAll(result)
                }.toMutableList()
            }
        }.onSuccess {
            _lastQuery = checkedQuery
            _lastOffset = it.size
        }
    }

    private fun checkSameQuery(query: String): String {
        return if (_lastQuery == query || query.isBlank()) {
            _lastQuery
        } else {
            _cachedList.clear()
            _lastOffset = 0
            query
        }
    }

}
