package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.AppConfigRepository
import com.bluepig.alarm.domain.repository.TubeRepository
import com.bluepig.alarm.domain.result.runSuspendCatching
import javax.inject.Inject

class GetTubeMediaList @Inject constructor(
    private val _repository: TubeRepository,
    private val _configRepository: AppConfigRepository,
) {
    private var _lastQuery = ""
    private var _lastNextPageToken: String? = null
    private var _cachedList = mutableListOf<TubeMedia>()

    suspend operator fun invoke(query: String) = runSuspendCatching {
        val onlyLinkSearch = _configRepository.getConfig().showOnlyYoutubeLink
        val tubeForId = _repository.checkTubeMedia(query)

        if (tubeForId != null || onlyLinkSearch) {
            clearCache()
            return@runSuspendCatching tubeForId?.let { listOf(it) } ?: emptyList()
        }
        val pair =
            if (_lastQuery == query && _lastNextPageToken != null) {
                _repository.getTubeList(query, _lastNextPageToken)
            } else {
                clearCache()
                _repository.getTubeList(query)
            }

        _lastNextPageToken = pair.second
        _lastQuery = query

        _cachedList.apply {
            addAll(pair.first)
        }.toList()
    }.onFailure {
        clearCache()
    }

    private fun clearCache() {
        _lastQuery = ""
        _lastNextPageToken = null
        _cachedList.clear()
    }
}
