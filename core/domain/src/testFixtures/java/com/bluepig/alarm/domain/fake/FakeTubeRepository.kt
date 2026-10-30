package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository

class FakeTubeRepository(
    private val linkedMedia: TubeMedia? = null,
    private val searchResult: List<TubeMedia> = emptyList(),
) : TubeRepository {
    var searchCount = 0

    override suspend fun getTubeList(query: String, nextPageToken: String?) =
        searchResult.also { searchCount++ } to null

    override suspend fun checkTubeMedia(query: String) = linkedMedia
    override suspend fun getAccountName(): String? = null
    override suspend fun setAccountName(name: String) = Unit
}
