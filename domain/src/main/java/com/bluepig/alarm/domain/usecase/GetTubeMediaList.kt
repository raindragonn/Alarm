package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTubeMediaList @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: TubeRepository
) {
    private var _lastQuery = ""
    private var _lastNextPageToken: String? = null
    private var _cachedList = mutableListOf<TubeMedia>()

    suspend operator fun invoke(query: String, onlyLinkSearch: Boolean) = withContext(_dispatcher) {
        kotlin.runCatching {
            val tubeForId = _repository.checkTubeMedia(query)

            if (tubeForId != null || onlyLinkSearch) {
                clearCache()
                return@runCatching tubeForId?.let { listOf<TubeMedia>(it) }
                    ?: emptyList()
            } else {
                val pair =
                    if (_lastQuery == query && _lastNextPageToken != null) {
                        _repository.getTubeList(query, _lastNextPageToken)
                    } else {
                        clearCache()
                        _repository.getTubeList(query)
                    }

                val result = pair.first
                _lastNextPageToken = pair.second
                _lastQuery = query

                _cachedList.apply {
                    addAll(result)
                }.toMutableList()
            }
        }.onFailure {
            clearCache()
        }
    }

    private fun clearCache() {
        _lastQuery = ""
        _lastNextPageToken = null
        _cachedList.clear()
    }
}