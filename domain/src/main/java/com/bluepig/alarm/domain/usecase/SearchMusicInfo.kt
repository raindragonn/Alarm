package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.domain.result.SearchQueryEmptyException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchMusicInfo @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: MusicInfoRepository
) {
    private var _lastQuery = ""
    private var _lastOffset = 0
    private var _cachedList = mutableListOf<MusicInfo>()
    suspend operator fun invoke(query: String = ""): Result<List<MusicInfo>> {
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
