package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository

class FakeTubeRepository(
    private val linkedMedia: TubeMedia? = null,
    private val searchResult: List<TubeMedia> = emptyList(),
    var accountName: String? = null,
    /** 검색할 때 던질 예외 (재인증 등) */
    var searchError: Throwable? = null,
) : TubeRepository {
    var searchCount = 0

    override suspend fun getTubeList(query: String, nextPageToken: String?): Pair<List<TubeMedia>, String?> {
        searchCount++
        searchError?.let { throw it }
        return searchResult to null
    }

    override suspend fun checkTubeMedia(query: String) = linkedMedia
    override suspend fun getAccountName(): String? = accountName
    override suspend fun setAccountName(name: String) {
        accountName = name
    }
}
